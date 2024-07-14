package com.san.kir.categories.ui.category

import com.san.kir.core.utils.viewModel.Action

internal sealed interface CategoryAction : Action {
    data object Save : CategoryAction
    data object Delete : CategoryAction
    data class Update(
        val newName: String? = null,
        val newTypeSort: String? = null,
        val newReverseSort: Boolean? = null,
        val newVisible: Boolean? = null,
        val newSpanPortrait: Int? = null,
        val newSpanLandscape: Int? = null,
    ) : CategoryAction
}
