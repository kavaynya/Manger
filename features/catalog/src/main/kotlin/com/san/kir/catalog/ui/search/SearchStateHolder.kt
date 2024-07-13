package com.san.kir.catalog.ui.search

import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.data.models.catalog.MiniCatalogItem
import kotlinx.coroutines.flow.StateFlow

internal interface SearchStateHolder : StateHolder<SearchState> {
    val items: StateFlow<List<MiniCatalogItem>>
}
