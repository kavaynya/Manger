package com.san.kir.library.ui.library

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.extend.CategoryWithMangas
import com.san.kir.data.db.main.views.ViewManga

internal sealed interface LibraryEvent : Action {
    data class SelectManga(val item: SimplifiedManga) : LibraryEvent
    data object NonSelect : LibraryEvent
    data class SetCurrentCategory(val item: CategoryWithMangas) : LibraryEvent
    data class ChangeCategory(val mangaId: Long, val categoryId: Long) : LibraryEvent
    data class DeleteManga(val mangaId: Long, val withFiles: Boolean) : LibraryEvent
    data object UpdateCurrentCategory : LibraryEvent
    data object UpdateAll : LibraryEvent
    data object UpdateApp : LibraryEvent
}
