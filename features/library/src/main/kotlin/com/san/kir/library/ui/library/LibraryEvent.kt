package com.san.kir.library.ui.library

import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.utils.viewModel.Event
import com.san.kir.data.models.main.SimplifiedManga
import com.san.kir.data.models.utils.MainMenuType

internal sealed interface LibraryEvent : Event {

    data class ToInfo(val id: Long, val params: SharedParams) : LibraryEvent
    data class ToStorage(val id: Long, val params: SharedParams) : LibraryEvent
    data class ToStats(val id: Long, val params: SharedParams) : LibraryEvent
    data class ToChapters(val id: Long, val params: SharedParams) : LibraryEvent
    data class ToOnline(val params: SharedParams) : LibraryEvent
    data class ToScreen(val menu: MainMenuType) : LibraryEvent
    data class ShowSelectedMangaDialog(val item: SimplifiedManga) : LibraryEvent
    data object DismissSelectedMangaDialog : LibraryEvent
}
