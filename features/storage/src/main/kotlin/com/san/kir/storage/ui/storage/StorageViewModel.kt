package com.san.kir.storage.ui.storage

import android.content.Context
import com.san.kir.background.works.AllChapterDelete
import com.san.kir.background.works.ChapterDeleteWorker
import com.san.kir.background.works.ReadChapterDelete
import com.san.kir.background.works.StoragesUpdateWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.StorageRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Storage
import com.san.kir.data.storageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class StorageViewModel(
    private val mangaId: Long,
    hasUpdate: Boolean,
    private val context: Context = ManualDI.application,
    private val storageRepository: StorageRepository = ManualDI.storageRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository()
) : ViewModel<StorageState>(), StorageStateHolder {

    private val backgroundState = MutableStateFlow<BackgroundState>(BackgroundState.None)
    private val storage = MutableStateFlow(Storage())
    private val mangaName = MutableStateFlow("")

    init {
        set(mangaId, hasUpdate)
    }

    override val tempState = combine(
        backgroundState,
        mangaName,
        storage,
        storageRepository.fullSize.distinctUntilChanged(),
        ::StorageState
    )
    override val defaultState = StorageState()

    override suspend fun onAction(action: Action) {
        when (action) {
            StorageAction.DeleteAll ->
                ChapterDeleteWorker.addTask<AllChapterDelete>(mangaId)

            StorageAction.DeleteRead ->
                ChapterDeleteWorker.addTask<ReadChapterDelete>(mangaId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun set(mangaId: Long, hasUpdate: Boolean) {
        if (hasUpdate) StoragesUpdateWorker.runTask()

        mangaRepository
            .loadItem(mangaId)
            .filterNotNull()
            .flatMapLatest {
                mangaName.value = it.name
                storageRepository.loadItemByPath(it.path)
            }
            .filterNotNull()
            .onEach { storage.value = it }
            .launchIn(this)

        combine(StoragesUpdateWorker.workInfos(), ChapterDeleteWorker.workInfos()) { stors, chaps ->
            backgroundState.update {
                when {
                    chaps.any { it.state.isFinished.not() } -> BackgroundState.Deleting
                    stors.any { it.state.isFinished.not() } -> BackgroundState.Load
                    else -> BackgroundState.None
                }
            }
        }.launchIn(this)
    }
}
