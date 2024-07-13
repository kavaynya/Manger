package com.san.kir.catalog.ui.catalogItem

import com.san.kir.catalog.R
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.catalog.SiteCatalogElement


internal data class CatalogItemState(
    val item: SiteCatalogElement = SiteCatalogElement(),
    val containingInLibrary: ContainingInLibraryState = ContainingInLibraryState.Check,
    val background: BackgroundState = BackgroundState.None,
) : ScreenState

internal sealed interface BackgroundState {
    data object Load : BackgroundState
    sealed class Error(val text: Int) : BackgroundState {
        data object Base : Error(R.string.load_info_failed_base)
        data object Auth : Error(R.string.load_info_failed_auth)
    }
    data object None : BackgroundState
}

internal enum class ContainingInLibraryState { Check, None, Ok }
