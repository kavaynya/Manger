package com.san.kir.catalog.ui.search

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.extend.MiniCatalogItem

internal data class SearchState(
    val items: List<MiniCatalogItem> = emptyList(),
    val background: Boolean = false
) : ScreenState

