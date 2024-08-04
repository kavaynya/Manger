package com.san.kir.chapters.ui.download

import com.san.kir.background.logic.DownloadChaptersManager
import com.san.kir.background.logic.di.downloadChaptersManager
import com.san.kir.core.internet.INetworkManager
import com.san.kir.core.internet.NetworkState
import com.san.kir.core.internet.cellularNetwork
import com.san.kir.core.internet.wifiNetwork
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.chapterRepository
import com.san.kir.data.db.main.repo.ChapterRepository
import com.san.kir.data.db.main.repo.SettingsRepository
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.data.settingsRepository

import kotlinx.coroutines.flow.combine

internal class DownloadsViewModel(
    private val chaptersRepository: ChapterRepository = ManualDI.chapterRepository(),
    private val cellularNetwork: INetworkManager = ManualDI.cellularNetwork(),
    private val wifiNetwork: INetworkManager = ManualDI.wifiNetwork(),
    private val manager: DownloadChaptersManager = ManualDI.downloadChaptersManager(),
    settingsRepository: SettingsRepository = ManualDI.settingsRepository(),
) : ViewModel<DownloadsState>(), DownloadsStateHolder {

    override val tempState = combine(
        chaptersRepository.downloadItems,
        cellularNetwork.state,
        wifiNetwork.state,
        settingsRepository.wifi,
    ) { items, cell, wifi, settingsWifi ->
        val network = if (settingsWifi) {
            if (wifi) NetworkState.OK else NetworkState.NOT_WIFI
        } else {
            if (cell || wifi) NetworkState.OK else NetworkState.NOT_CELLULAR
        }
        DownloadsState(
            items = items.groupBy { it.status.groupName }
                .map { (title, items) -> DownloadGroup(title, items) },
            network = network
        )
    }

    override val defaultState = DownloadsState()

    override suspend fun onAction(action: Action) {
        when (action) {
            DownloadsAction.ClearAll -> clearAll()
            DownloadsAction.CompletedClear -> clearCompleted()
            DownloadsAction.ErrorClear -> clearError()
            DownloadsAction.PausedClear -> clearPaused()
            DownloadsAction.StartAll -> manager.addPausedTasks()
            DownloadsAction.StopAll -> manager.removeAllTasks()
            is DownloadsAction.StartDownload -> manager.addTask(action.itemId)
            is DownloadsAction.StopDownload -> manager.removeTask(action.itemId)
        }
    }

    private suspend fun clearAll() {
        clearPaused()
        clearCompleted()
        clearError()
    }

    private suspend fun clearError() {
        chaptersRepository.updateFor(DownloadState.ERROR)
    }

    private suspend fun clearPaused() {
        chaptersRepository.updateFor(DownloadState.PAUSED)
    }

    private suspend fun clearCompleted() {
        chaptersRepository.updateFor(DownloadState.COMPLETED)
    }

    override fun onDestroy() {
        cellularNetwork.stop()
        wifiNetwork.stop()
    }
}
