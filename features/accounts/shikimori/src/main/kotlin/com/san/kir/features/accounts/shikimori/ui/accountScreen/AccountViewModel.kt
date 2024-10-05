package com.san.kir.features.accounts.shikimori.ui.accountScreen

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.coroutines.defaultExcLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.accounts.shikimori.logic.Helper
import com.san.kir.features.accounts.shikimori.logic.HelperImpl
import com.san.kir.features.accounts.shikimori.logic.di.accountItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.authRepository
import com.san.kir.features.accounts.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.accounts.shikimori.logic.repo.AuthRepository
import com.san.kir.features.accounts.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.useCases.BindingUseCase
import com.san.kir.features.accounts.shikimori.ui.accountItem.LoginState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
internal class AccountViewModel(
    private val accountId: Long,
    private val authRepository: AuthRepository = ManualDI.authRepository(),
    private val accountItemRepository: AccountItemRepository = ManualDI.accountItemRepository(
        accountId
    ),
    libraryRepository: LibraryItemRepository = ManualDI.libraryItemRepository(),
) : ViewModel<AccountState>(), AccountStateHolder, Helper<AccountMangaItem> by HelperImpl() {

    private var updateJob: Job? = null
    private val bindingHelper = BindingUseCase(libraryRepository)

    private val loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    // Список элементов из БД
    private val dbItems = loginState
        .filterIsInstance<LoginState.Ok>()
        .distinctUntilChanged { old, new -> old.nickName == new.nickName }
        .onStart { updateDataFromNetwork() }
        .flatMapLatest { accountItemRepository.loadItems() }
        .distinctUntilChanged()
        .onStart { emit(emptyList()) }
        .flowOn(defaultDispatcher)

    init {
        dbItems
            // Отфильтровка не привязанных элементов
            .mapLatest(bindingHelper.prepareData())
            .onEach(send(true))
            // Проверка каждого элемента на возможность привязки
            .flatMapLatest(bindingHelper.checkBinding())
            .onEach(send())
            .flowOn(defaultDispatcher)
            .launch()

        // Данные об авторизации
        accountItemRepository.authData
            .catch { loginState.value = LoginState.Error }
            .onEach { auth ->
                loginState.value = if (auth.isLogin) {
                    LoginState.Ok(accountId, auth.user.nickname, auth.user.avatar)
                } else {
                    LoginState.LogOut
                }
            }
            .launch()
    }


    override val tempState = combine(loginState, hasAction, ::AccountState)
    override val defaultState = AccountState()

    override val boundedItems = dbItems.map(bindingHelper.filterData())
        .stateInSubscribed(emptyList())
    override val unboundedItems = unbindedItems

    override suspend fun onAction(action: Action) {
        when (action) {
            is AccountAction.LogOut -> {
                loginState.value = LoginState.Loading
                authRepository.logout(accountId, action.full)
                sendEvent(AccountEvent.ToBack)
            }

            AccountAction.Update -> updateDataFromNetwork()
        }
    }

    private fun updateDataFromNetwork() {
        if (updateJob?.isActive == true) return
        updateJob = defaultExcLaunch(onFailure = { updateLoading(false) }) {
            updateLoading(true)
            accountItemRepository.refreshRates()
            updateLoading(false)
        }
    }

    companion object {
        private const val TAG: String = "AccountViewModel"
    }
}
