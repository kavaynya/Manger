package com.san.kir.chapters.ui.latest

import android.content.Context
import com.san.kir.background.logic.DownloadChaptersManager
import com.san.kir.background.logic.di.downloadChaptersManager
import com.san.kir.background.util.collectWorkInfoByTag
import com.san.kir.background.works.LatestClearWorkers
import com.san.kir.chapters.logic.di.latestRepository
import com.san.kir.chapters.logic.repo.LatestRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.models.utils.ChapterStatus
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.entites.action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
internal class LatestViewModel(
    private val context: Context = ManualDI.context,
    private val latestRepository: LatestRepository = ManualDI.latestRepository,
    private val downloadManager: DownloadChaptersManager = ManualDI.downloadChaptersManager,
) : ViewModel<LatestState>(), LatestStateHolder {

    private var job: Job? = null
    private val hasBackground = MutableStateFlow(true)
    private val items = MutableStateFlow<List<SelectableItem>>(emptyList())

    private val newItems = latestRepository
        .notReadItems
        .distinctUntilChanged()
        .flowOn(defaultDispatcher)
        .mapLatest { list -> list.filter { it.action == ChapterStatus.DOWNLOADABLE } }

    init {
        latestRepository
            .items
            .onEach { list ->
                items.update { oldItems ->
                    if (list.size != oldItems.size) {
                        list.map { SelectableItem(it, false) }
                            
                    } else {
                        list.zip(oldItems)
                            .map { (chapter, item) -> item.copy(chapter = chapter) }
                            
                    }
                }
            }
            .flowOn(defaultDispatcher)
            .launchIn(viewModelScope)
    }

    override val tempState = combine(
        items,
        newItems.onEach { runWorkersObserver() },
        hasBackground,
    ) { items, newItems, background ->
        LatestState(
            items = items,
            hasNewChapters = newItems.isNotEmpty(),
            hasBackgroundWork = background,
        )
    }

    override val defaultState = LatestState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            LatestEvent.CleanAll -> LatestClearWorkers.clearAll(context)
            LatestEvent.CleanDownloaded -> LatestClearWorkers.clearDownloaded(context)
            LatestEvent.CleanRead -> LatestClearWorkers.clearReaded(context)
            LatestEvent.DownloadNew -> downloadNewChapters()
            LatestEvent.RemoveSelected -> removeSelected()
            LatestEvent.DownloadSelected -> downloadSelected()
            LatestEvent.UnselectAll -> unselect()
            is LatestEvent.ChangeSelect -> changeSelect(event.index)
            is LatestEvent.StartDownload -> downloadManager.addTask(event.id)
            is LatestEvent.StopDownload -> downloadManager.removeTask(event.id)
        }
    }

    private suspend fun downloadNewChapters() {
        downloadManager.addTasks(newItems.first().map { it.id })
    }

    private suspend fun removeSelected() {
        latestRepository.update(items.value.filter { it.selected }.map { it.chapter.id }, false)
    }

    private suspend fun downloadSelected() {
        downloadManager.addTasks(items.value.filter { it.selected }.map { it.chapter.id })
    }

    private fun changeSelect(index: Int) {
        items.update { list ->
            val changedItem = list[index]
            list.set(index, changedItem.copy(selected = changedItem.selected.not()))
        }
    }

    private fun unselect() {
        items.update { list -> list.map { it.copy(selected = false) } }
    }

    private fun runWorkersObserver() {
        if (job?.isActive == true) return
        hasBackground.value = false
        job = viewModelScope.defaultLaunch {
            collectWorkInfoByTag(LatestClearWorkers.TAG) { works ->
                hasBackground.value = works.any { it.state.isFinished.not() }
            }
        }
    }
}
