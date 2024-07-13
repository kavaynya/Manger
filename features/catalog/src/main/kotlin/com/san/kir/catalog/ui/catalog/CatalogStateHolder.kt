package com.san.kir.catalog.ui.catalog

import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.data.models.catalog.MiniCatalogItem
import kotlinx.coroutines.flow.StateFlow

internal interface CatalogStateHolder : StateHolder<CatalogState> {
    val filters: StateFlow<List<Filter>>
    val items: StateFlow<List<MiniCatalogItem>>
    val filterState: StateFlow<FilterState>
    val sortState: StateFlow<SortState>
    val backgroundWork: StateFlow<BackgroundState>
}
