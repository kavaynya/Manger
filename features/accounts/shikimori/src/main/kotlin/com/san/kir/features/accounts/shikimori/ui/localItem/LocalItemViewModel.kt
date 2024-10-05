package com.san.kir.features.accounts.shikimori.ui.localItem

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.accounts.shikimori.logic.di.accountItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.syncManager
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.accounts.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.accounts.shikimori.ui.syncManager.ISyncManager
import com.san.kir.features.accounts.shikimori.ui.syncManager.checkSync
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocalItemViewModel(
    accountId: Long,
    mangaId: Long?,
    private val libraryItemRepository: LibraryItemRepository = ManualDI.libraryItemRepository(),
    private val accountItemRepository: AccountItemRepository =
        ManualDI.accountItemRepository(accountId),
    private val syncManager: ISyncManager<LibraryMangaItem> =
        ManualDI.syncManager(accountId, accountItemRepository),
) : ViewModel<LocalItemState>(), LocalItemStateHolder {
    private var mangaJob: Job? = null
    private val mangaState = MutableStateFlow<MangaState>(MangaState.Load)
    private val profileState = MutableStateFlow<ProfileState>(ProfileState.None)

    init {
        mangaState
            .filterIsInstance<MangaState.Ok>()
            .distinctUntilChanged()
            .onEach { syncManager.checkSync(it.item) }
            .flowOn(defaultDispatcher)
            .launch()

        launch { sendAction(LocalItemAction.Update(mangaId)) }
    }

    override val tempState = combine(mangaState, syncManager.state, profileState, ::LocalItemState)
    override val defaultState = LocalItemState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is LocalItemAction.Update -> setId(action.mangaId)
        }
    }

    private suspend fun setId(mangaId: Long?) {
        Timber.v("setId")
        if (mangaId != null && mangaId != -1L) {
            mangaJob?.cancelAndJoin()
            mangaState.value = MangaState.Load
            mangaJob = libraryItemRepository.loadItemById(mangaId)
                .distinctUntilChanged()
                .filterNotNull()
                .catch { mangaState.value = MangaState.Error }
                .mapLatest(MangaState::Ok)
                .onEach(mangaState::emit)
                .launch()
        }
    }

}

