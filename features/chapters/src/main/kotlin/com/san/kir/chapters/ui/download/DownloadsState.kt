package com.san.kir.chapters.ui.download

import com.san.kir.core.internet.NetworkState
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.custom.DownloadChapter

internal data class DownloadsState(
    val network: NetworkState = NetworkState.OK,
    val items: List<DownloadChapter> = emptyList(),
    val loadingCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.QUEUED || it.status == com.san.kir.data.models.utils.DownloadState.LOADING },
    val stoppedCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.PAUSED },
    val completedCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.COMPLETED },
) : ScreenState
