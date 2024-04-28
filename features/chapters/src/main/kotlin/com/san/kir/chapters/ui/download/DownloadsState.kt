package com.san.kir.chapters.ui.download

import com.san.kir.core.internet.NetworkState
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.extend.DownloadChapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


internal data class DownloadsState(
    val network: NetworkState = NetworkState.OK,
    val items: ImmutableList<DownloadChapter> = persistentListOf(),
    val loadingCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.QUEUED || it.status == com.san.kir.data.models.utils.DownloadState.LOADING },
    val stoppedCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.PAUSED },
    val completedCount: Int = items.count { it.status == com.san.kir.data.models.utils.DownloadState.COMPLETED },
) : ScreenState
