package com.san.kir.chapters.ui.latest

import com.san.kir.core.utils.viewModel.Action


internal sealed interface LatestAction : Action {
    data object DownloadNew : LatestAction
    data object CleanAll : LatestAction
    data object CleanRead : LatestAction
    data object CleanDownloaded : LatestAction
    data object RemoveSelected : LatestAction
    data object DownloadSelected : LatestAction
    data object UnselectAll : LatestAction
    data object SelectAll : LatestAction
    data class ChangeSelect(val itemIds: List<Long>) : LatestAction
    data class StartDownload(val id: Long) : LatestAction
    data class StopDownload(val id: Long) : LatestAction

    companion object {
        fun changeSelect(vararg itemId: Long) = ChangeSelect(itemId.toList())
    }
}

