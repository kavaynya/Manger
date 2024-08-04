package com.san.kir.chapters.ui.latest

import com.san.kir.core.utils.viewModel.Event

internal sealed interface LatestEvent : Event {
    data class ToViewer(val id: Long) : LatestEvent
}
