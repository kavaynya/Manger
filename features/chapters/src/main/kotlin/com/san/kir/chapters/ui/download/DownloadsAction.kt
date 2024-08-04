package com.san.kir.chapters.ui.download

import com.san.kir.core.utils.viewModel.Action


internal sealed interface DownloadsAction : Action {
    data object StartAll : DownloadsAction
    data object StopAll : DownloadsAction
    data object ClearAll : DownloadsAction
    data object CompletedClear : DownloadsAction
    data object PausedClear : DownloadsAction
    data object ErrorClear : DownloadsAction
    data class StartDownload(val itemId: Long) : DownloadsAction
    data class StopDownload(val itemId: Long) : DownloadsAction
}
