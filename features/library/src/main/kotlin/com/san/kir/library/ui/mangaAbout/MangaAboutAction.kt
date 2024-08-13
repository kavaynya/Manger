package com.san.kir.library.ui.mangaAbout

import com.san.kir.core.utils.viewModel.Action

internal sealed interface MangaAboutAction : Action {
    data class ChangeUpdate(val newState: Boolean) : MangaAboutAction
}
