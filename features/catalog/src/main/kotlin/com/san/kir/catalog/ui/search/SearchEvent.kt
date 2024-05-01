package com.san.kir.catalog.ui.search

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.extend.MiniCatalogItem


internal sealed interface SearchEvent : Action {
    data class Search(val query: String) : SearchEvent
    data class UpdateManga(val item: MiniCatalogItem) : SearchEvent
}
