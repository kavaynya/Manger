package com.san.kir.categories.ui.category

import com.san.kir.core.utils.viewModel.Action

internal sealed interface CategoryEvent : Action {
    data class Set(val categoryName: String) : CategoryEvent
    data object Save : CategoryEvent
    data object Delete : CategoryEvent
    data class Update(
        val newName: String? = null,
        val newTypeSort: String? = null,
        val newReverseSort: Boolean? = null,
        val newVisible: Boolean? = null,
        val newLargePortrait: Boolean? = null,
        val newSpanPortrait: Int? = null,
        val newLargeLandscape: Boolean? = null,
        val newSpanLandscape: Int? = null,
    ) : CategoryEvent
}
