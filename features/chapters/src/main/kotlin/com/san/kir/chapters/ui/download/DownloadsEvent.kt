package com.san.kir.chapters.ui.download

import com.san.kir.core.utils.viewModel.Event

internal sealed interface DownloadsEvent : Event {
    data object ShowDeleteMenu : DownloadsEvent
    data object HideDeleteMenu : DownloadsEvent
}
