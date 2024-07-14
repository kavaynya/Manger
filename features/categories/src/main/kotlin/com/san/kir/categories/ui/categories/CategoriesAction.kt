package com.san.kir.categories.ui.categories

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.main.Category

internal sealed interface CategoriesAction : Action {
    data class ChangeVisibility(val item: Category, val newState: Boolean) : CategoriesAction
    data class Reorder(val from: Int, val to: Int) : CategoriesAction
}
