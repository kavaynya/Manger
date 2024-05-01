package com.san.kir.catalog.ui.catalog

import com.san.kir.core.utils.viewModel.StateHolder

import kotlinx.coroutines.flow.StateFlow

internal interface CatalogStateHolder : StateHolder<CatalogState> {
    val filters: StateFlow<List<Filter>>
}
