package com.san.kir.chapters.ui.download

import com.san.kir.core.internet.NetworkState
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.DownloadItem
import com.san.kir.data.models.utils.DownloadState

internal data class DownloadsState(
    val network: NetworkState = NetworkState.OK,
    val items: List<DownloadGroup> = emptyList(),
) : ScreenState {
    val allCount: Int = items.count()
    val loadingCount: Int = items.sumOf { it.loadingCount }
    val stoppedCount: Int = items.sumOf { it.stoppedCount }
    val completedCount: Int = items.sumOf { it.completedCount }
    val errorCount: Int = items.sumOf { it.errorCount }
    val canClearedCount: Int = completedCount + stoppedCount + errorCount

}

internal data class DownloadGroup(
    val groupName: Int,
    val items: List<DownloadItem>
) {
    val itemsCount = items.size
    val loadingCount = items.count { it.status == DownloadState.QUEUED || it.status == DownloadState.LOADING }
    val stoppedCount = items.count { it.status == DownloadState.PAUSED }
    val completedCount = items.count { it.status == DownloadState.COMPLETED }
    val errorCount  = items.count { it.status == DownloadState.ERROR }
}
