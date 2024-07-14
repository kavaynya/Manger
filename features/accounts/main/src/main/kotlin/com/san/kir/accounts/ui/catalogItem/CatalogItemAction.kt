package com.san.kir.accounts.ui.catalogItem

import com.san.kir.core.utils.viewModel.Action

internal sealed interface CatalogItemAction : Action {
    data object Update : CatalogItemAction
}
