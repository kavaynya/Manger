package com.san.kir.chapters.ui.chapters

import com.san.kir.core.utils.viewModel.Event

internal sealed interface ChaptersEvent : Event {
    data object ShowDeleteDialog : ChaptersEvent
    data object ShowFullDeleteDialog : ChaptersEvent
    data object ShowFullResetDialog : ChaptersEvent
    data object ToGlobalSearch : ChaptersEvent
    data class ToViewer(val id: Long) : ChaptersEvent
}
