package com.san.kir.chapters.ui.latest

import com.san.kir.core.utils.viewModel.Action


internal sealed interface LatestEvent : Action {
    data object DownloadNew : LatestEvent
    data object CleanAll : LatestEvent
    data object CleanRead : LatestEvent
    data object CleanDownloaded : LatestEvent
    data object RemoveSelected : LatestEvent
    data object DownloadSelected : LatestEvent
    data object UnselectAll : LatestEvent
    data class ChangeSelect(val index: Int) : LatestEvent
    data class StartDownload(val id: Long) : LatestEvent
    data class StopDownload(val id: Long) : LatestEvent
}
