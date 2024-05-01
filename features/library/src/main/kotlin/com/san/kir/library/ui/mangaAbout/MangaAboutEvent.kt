package com.san.kir.library.ui.mangaAbout

import com.san.kir.core.utils.viewModel.Action

internal sealed interface MangaAboutEvent : Action {
    data class Set(val id: Long) : MangaAboutEvent
    data class ChangeUpdate(val newState: Boolean) : MangaAboutEvent
    data class ChangeColor(val newState: Int) : MangaAboutEvent
}
