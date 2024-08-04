package com.san.kir.chapters.ui.chapters

import com.san.kir.core.utils.viewModel.Action

internal sealed interface ChaptersAction : Action {
    data class WithSelected(val mode: Selection) : ChaptersAction
    data class ChangeFilter(val mode: Filter) : ChaptersAction
    data class StartDownload(val id: Long) : ChaptersAction
    data class StopDownload(val id: Long) : ChaptersAction
    data object UpdateManga : ChaptersAction
    data object DownloadNext : ChaptersAction
    data object DownloadNotRead : ChaptersAction
    data object DownloadAll : ChaptersAction
    data object ChangeIsUpdate : ChaptersAction
    data object ChangeMangaSort : ChaptersAction
    data object FullReset : ChaptersAction
    data object ResetError : ChaptersAction
}

internal sealed interface Selection {
    data object Download : Selection
    data object All : Selection
    data object Clear : Selection
    data object Above : Selection
    data object Below : Selection
    data object DeleteFromDB : Selection
    data object DeleteFiles : Selection
    data object Reset : Selection
    data class Change(val index: Int) : Selection
    data class SetRead(val newState: Boolean) : Selection
}

internal sealed interface Filter {
    data object Reverse : Filter
    data object All : Filter
    data object Read : Filter
    data object NotRead : Filter
}


