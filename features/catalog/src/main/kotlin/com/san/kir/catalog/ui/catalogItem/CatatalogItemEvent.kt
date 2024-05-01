package com.san.kir.catalog.ui.catalogItem

import com.san.kir.core.utils.viewModel.Action


internal sealed interface CatalogItemEvent : Action {
    data class Set(val url: String) : CatalogItemEvent
}
