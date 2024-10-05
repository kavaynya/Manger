package com.san.kir.features.accounts.shikimori.ui.localItems

import com.san.kir.core.utils.viewModel.Action

internal sealed interface LocalItemsAction : Action {
    data object Update : LocalItemsAction
}
