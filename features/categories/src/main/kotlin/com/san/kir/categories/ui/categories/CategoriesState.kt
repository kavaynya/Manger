package com.san.kir.categories.ui.categories

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.Category

@Stable
internal data class CategoriesState(
    val items: List<Category> = emptyList()
) : ScreenState
