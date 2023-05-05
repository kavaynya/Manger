package com.san.kir.features.shikimori.ui.localItem

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.flow.Result
import com.san.kir.core.utils.flow.asResult
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.ShikiDbManga
import com.san.kir.data.models.extend.SimplifiedMangaWithChapterCounts
import com.san.kir.features.shikimori.logic.SyncDialogEvent
import com.san.kir.features.shikimori.logic.SyncDialogState
import com.san.kir.features.shikimori.logic.SyncManager
import com.san.kir.features.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.shikimori.logic.di.profileItemRepository
import com.san.kir.features.shikimori.logic.di.syncManager
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import com.san.kir.features.shikimori.logic.useCases.SyncState
import com.san.kir.features.shikimori.logic.useCases.SyncUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocalItemViewModel(
    private val libraryItemRepository: LibraryItemRepository = ManualDI.libraryItemRepository,
    private val syncManager: SyncManager = ManualDI.syncManager,
    profileItemRepository: ProfileItemRepository = ManualDI.profileItemRepository,
) : ViewModel<LocalItemState>(), LocalItemStateHolder {
    private val syncCheck = SyncUseCase<SimplifiedMangaWithChapterCounts>(profileItemRepository)
    private var mangaJob: Job? = null
    private val mangaState = MutableStateFlow<MangaState>(MangaState.Load)
    private val profileState = MutableStateFlow<ProfileState>(ProfileState.None)

    init {
        mangaState
            .filterIsInstance<MangaState.Ok>()
            .distinctUntilChanged()
            .onEach { syncCheck.launchSyncCheck(it.item) }
            .flowOn(defaultDispatcher)
            .launchIn(viewModelScope)

        syncManager.beforeBindChange { syncCheck.findingSyncCheck() }
        syncManager.onBindChange { syncCheck.launchSyncCheck() }
    }

    override val tempState = combine(
        mangaState,
        syncCheck.syncState,
        syncManager.dialogState,
        profileState,
        ::LocalItemState
    )

    override val defaultState = LocalItemState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is LocalItemEvent.Update -> setId(event.mangaId)
            is LocalItemEvent.Sync -> onSyncEvent(event.event)
        }
    }

    private suspend fun onSyncEvent(event: SyncDialogEvent) {
        when (event) {
            SyncDialogEvent.DialogDismiss -> syncManager.dialogNone()
            is SyncDialogEvent.SyncCancel -> syncManager.cancelSync(event.rate)
            is SyncDialogEvent.SyncToggle ->
                when (state.value.sync) {
                    is SyncState.Founds -> syncManager.initSync(event.item)
                    is SyncState.Ok -> {
                        val onlineManga = event.item as ShikiDbManga
                        syncManager.askCancelSync(onlineManga.rate)
                    }
                    else -> {}
                }
            is SyncDialogEvent.SyncNext -> {
                when (val currentState = state.value.dialog) {
                    is SyncDialogState.Init -> {
                        val manga = state.value.manga
                        if (manga !is MangaState.Ok) return

                        val onlineManga = currentState.manga as ShikiDbManga

                        syncManager.checkAllChapters(onlineManga.all, onlineManga.rate, manga.item)
                    }
                    is SyncDialogState.DifferentChapterCount ->
                        syncManager.checkReadChapters(currentState.profileRate, currentState.manga)
                    is SyncDialogState.DifferentReadCount -> {
                        syncManager.launchSync(
                            currentState.profileRate, currentState.manga, event.onlineIsTruth
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun setId(mangaId: Long?) {
        Timber.v("setId")
        if (mangaId != null && mangaId != -1L) {
            mangaJob?.cancelAndJoin()
            mangaJob = libraryItemRepository.loadItemById(mangaId)
                .distinctUntilChanged()
                .filterNotNull()
                .asResult()
                .mapLatest { result ->
                    when (result) {
                        is Result.Error -> MangaState.Error
                        Result.Loading -> MangaState.Load
                        is Result.Success -> MangaState.Ok(result.data)
                    }
                }
                .onEach(mangaState::emit)
                .launchIn(viewModelScope)
        }
    }

}

