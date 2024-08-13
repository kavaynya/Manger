package com.san.kir.library.ui.library

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.main.CategoryWithMangas

internal sealed interface LibraryAction : Action {
    data class SetCurrentCategory(val item: CategoryWithMangas) : LibraryAction
    data class ChangeCategory(val mangaId: Long, val categoryId: Long) : LibraryAction
    data class DeleteManga(val mangaId: Long, val withFiles: Boolean) : LibraryAction
    data class ChangeColor(val mangaId: Long, val color: Int) : LibraryAction
    data object UpdateCurrentCategory : LibraryAction
    data object UpdateAll : LibraryAction
    data object UpdateApp : LibraryAction
}
