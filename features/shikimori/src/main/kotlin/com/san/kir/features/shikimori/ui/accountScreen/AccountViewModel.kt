package com.san.kir.features.shikimori.ui.accountScreen

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.coroutines.defaultExcLaunch
import com.san.kir.core.utils.flow.Result
import com.san.kir.core.utils.flow.asResult
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.ShikiDbManga
import com.san.kir.features.shikimori.logic.Helper
import com.san.kir.features.shikimori.logic.HelperImpl
import com.san.kir.features.shikimori.logic.di.authUseCase
import com.san.kir.features.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.shikimori.logic.di.profileItemRepository
import com.san.kir.features.shikimori.logic.di.settingsRepository
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import com.san.kir.features.shikimori.logic.repo.SettingsRepository
import com.san.kir.features.shikimori.logic.useCases.AuthUseCase
import com.san.kir.features.shikimori.logic.useCases.BindingUseCase
import com.san.kir.features.shikimori.ui.accountItem.LoginState
import com.san.kir.features.shikimori.ui.util.DialogState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
internal class AccountViewModel(
    private val authUseCase: AuthUseCase = ManualDI.authUseCase,
    private val profileRepository: ProfileItemRepository = ManualDI.profileItemRepository,
    private val settingsRepository: SettingsRepository = ManualDI.settingsRepository,
    libraryRepository: LibraryItemRepository = ManualDI.libraryItemRepository,
) : ViewModel<AccountState>(), AccountStateHolder, Helper<ShikiDbManga> by HelperImpl() {
    private var updateJob: Job? = null
    private val bindingHelper = BindingUseCase(libraryRepository)

    private val loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    private val dialogState = MutableStateFlow<DialogState>(DialogState.Hide)

    // Список элементов из БД
    private val dbItems = loginState
        .filterIsInstance<LoginState.LogInOk>()
        .distinctUntilChanged { old, new -> old.nickName == new.nickName }
        .flatMapLatest {
            profileRepository.loadItems()
                // Обновить данные из сети при первом запросе к бд
                .onStart { updateDataFromNetwork() }
        }
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
            .launchIn(viewModelScope)

        // Данные об авторизации
        authUseCase.authData.asResult()
            .map { auth ->
                when (auth) {
                    is Result.Error -> LoginState.Error
                    Result.Loading -> LoginState.Loading
                    is Result.Success -> {
                        if (auth.data.isLogin) {
                            LoginState.LogInOk(auth.data.nickName)
                        } else {
                            LoginState.LogOut
                        }
                    }
                }
            }
            .onEach { state -> loginState.value = state }
            .launchIn(viewModelScope)
    }


    override val tempState = combine(
        loginState,
        dialogState,
        // Манга из олайн-профиля с уже существующей привязкой
        dbItems.map(bindingHelper.filterData()),
        unbindedItems,
        hasAction
    ) { login, dialog, bind, unbind, action ->
        AccountState(login, dialog, action, ScreenItems(bind, unbind))
    }

    override val defaultState = AccountState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            AccountEvent.LogOut -> when (dialogState.value) {
                DialogState.Hide -> {
                    dialogState.update { DialogState.Show }
                }

                DialogState.Show -> {
                    dialogState.update { DialogState.Hide }
                    loginState.update { LoginState.Loading }
                    authUseCase.logout()
                }
            }

            AccountEvent.CancelLogOut -> when (dialogState.value) {
                DialogState.Hide -> {}
                DialogState.Show -> {
                    dialogState.update { DialogState.Hide }
                }
            }

            AccountEvent.Update -> updateDataFromNetwork()
        }
    }

    private fun updateDataFromNetwork() {
        if (updateJob?.isActive == true) return
        updateJob = viewModelScope.defaultExcLaunch(
            onFailure = { updateLoading(false) }
        ) {
            updateLoading(true)

            profileRepository.updateRates(settingsRepository.currentAuth())

            updateLoading(false)
        }
    }
}
