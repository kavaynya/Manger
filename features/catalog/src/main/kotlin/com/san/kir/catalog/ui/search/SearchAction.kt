package com.san.kir.catalog.ui.search

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.catalog.MiniCatalogItem


internal sealed interface SearchAction : Action {
    data class Search(val query: String) : SearchAction
    data class UpdateManga(val item: MiniCatalogItem) : SearchAction
    data class ChangeCatalogSelect(val name: String) : SearchAction
    data class ChangeAddMangaVisible(val state: Boolean) : SearchAction
    data object ApplyCatalogFilter : SearchAction
}
