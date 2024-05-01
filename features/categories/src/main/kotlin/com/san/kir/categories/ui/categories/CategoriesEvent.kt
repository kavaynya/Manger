package com.san.kir.categories.ui.categories

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.base.Category

internal sealed interface CategoriesEvent : Action {
    data class ChangeVisibility(val item: Category, val newState: Boolean) : CategoriesEvent
    data class Reorder(val from: Int, val to: Int) : CategoriesEvent
}
