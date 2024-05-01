package com.san.kir.categories.ui.category

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.base.Category

internal data class CategoryState(
    val category: Category = Category(),
    val categoryNames: List<String> = emptyList(),
    val hasCreatedNew: Boolean = false,
    val oldCategoryName: String = "",
    val hasAll: Boolean = false,
    val hasChanges: Boolean = false,
) : ScreenState
