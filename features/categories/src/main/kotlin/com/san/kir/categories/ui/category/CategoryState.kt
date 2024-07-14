package com.san.kir.categories.ui.category

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.Category

internal data class CategoryState(
    val category: Category = Category(),
    val isCreatedNew: Boolean = false,
    val oldCategoryName: String = "",
    val hasAll: Boolean = false,
    val hasChanges: Boolean = false,
    val error: ErrorState = ErrorState.None
) : ScreenState

internal sealed class ErrorState(val has: Boolean, val validate: String) {
    data object None : ErrorState(has = false, validate = "")
    class Has(validate: String) : ErrorState(has = true, validate = validate)
}
