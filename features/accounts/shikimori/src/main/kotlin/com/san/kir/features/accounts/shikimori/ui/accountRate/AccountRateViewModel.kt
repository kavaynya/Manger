package com.san.kir.features.accounts.shikimori.ui.accountRate

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.accounts.shikimori.logic.di.accountItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.syncManager
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.Auth
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.accounts.shikimori.ui.syncManager.ISyncManager
import com.san.kir.features.accounts.shikimori.ui.syncManager.checkSync
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber


internal class AccountRateViewModel(
    val accountId: Long,
    private val mangaItem: AccountMangaItem,
    private val accountRepository: AccountItemRepository = ManualDI.accountItemRepository(accountId),
    private val syncManager: ISyncManager<AccountMangaItem> =
        ManualDI.syncManager(accountId, ManualDI.libraryItemRepository()),
) : ViewModel<AccountRateState>(), AccountRateStateHolder {
    private val itemState = MutableStateFlow<ItemState>(ItemState.Ok)
    private val item = MutableStateFlow(mangaItem)
    private val hasLoading = MutableStateFlow(false)
    private val auth = accountRepository.authData.stateInEagerly(Auth())

    private var lastCanRepeatedAction: Action? = null
    private var itemSyncJob: Job? = null

    init {
        startItemSync(mangaItem.idInAccount)
        item.onEach(syncManager::checkSync).launch()
    }

    override val tempState = combine(
        syncManager.state, item, itemState, hasLoading, ::AccountRateState
    )

    override val defaultState = AccountRateState()

    override suspend fun onAction(action: Action) {
        when (action) {
            AccountRateAction.Add -> {
                lastCanRepeatedAction = action
                addToProfile()
            }

            AccountRateAction.Remove -> {
                lastCanRepeatedAction = action
                removeFromProfile()
            }

            is AccountRateAction.Update -> {
                lastCanRepeatedAction = action
                update()
            }

            is AccountRateAction.Change -> {
                lastCanRepeatedAction = action
                change(action.item)
            }

            AccountRateAction.TryAgainLastAction -> invokeLastAction()
            else -> syncManager.sendAction(action)
        }
    }

    private suspend fun update() {
        runCatching { accountRepository.refresh(idInAccount = 0L, idInSite = mangaItem.idInSite) }
    }

    private suspend fun addToProfile() {
        runCatching { startItemSync(accountRepository.add(item.value.idInSite)) }
    }

    private suspend fun removeFromProfile() {
        runCatching {
            accountRepository.remove(item.value.idInAccount)
            item.update { old -> old.copy(idInAccount = -1L) }
            stopItemSync()
        }
    }

    private suspend fun change(item: AccountMangaItem) {
        runCatching { accountRepository.update(item) }
    }

    private fun invokeLastAction() {
        lastCanRepeatedAction?.let(::sendAction)
    }

    private fun startItemSync(idInAccount: Long) {
        if (idInAccount == -1L) return

        itemSyncJob = accountRepository.loadItemById(idInAccount)
            .filterNotNull()
            .onEach { item.value = it }
            .launch()
    }

    private fun stopItemSync() {
        itemSyncJob?.cancel()
        itemSyncJob = null
    }

    private suspend fun runCatching(action: suspend () -> Unit) {
        hasLoading.value = true
        kotlin.runCatching {
            action()
            itemState.value = ItemState.Ok
            lastCanRepeatedAction = null
        }.onFailure {
            itemState.value = ItemState.Error
            Timber.tag(TAG).e(it)
        }
        hasLoading.value = false
    }

    companion object {
        private const val TAG = "AccountRateViewModel"
    }
}
