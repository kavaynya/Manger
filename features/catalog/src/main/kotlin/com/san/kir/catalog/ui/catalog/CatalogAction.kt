package com.san.kir.catalog.ui.catalog

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.catalog.MiniCatalogItem


internal sealed interface CatalogAction : Action {
    data class Search(val query: String) : CatalogAction
    data class ChangeFilter(val type: FilterType, val index: Int) : CatalogAction
    data class ChangeSort(val sort: SortType) : CatalogAction
    data class UpdateManga(val item: MiniCatalogItem) : CatalogAction
    data object Reverse : CatalogAction
    data object ClearFilters : CatalogAction
    data object UpdateContent : CatalogAction
    data object CancelUpdateContent : CatalogAction
}
