package com.san.kir.catalog.ui.catalog

import com.san.kir.core.utils.viewModel.StateHolder
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow

internal interface CatalogStateHolder : StateHolder<CatalogState> {
    val filters: StateFlow<PersistentList<Filter>>
}
