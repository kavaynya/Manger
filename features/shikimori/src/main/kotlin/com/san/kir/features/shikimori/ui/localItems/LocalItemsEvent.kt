package com.san.kir.features.shikimori.ui.localItems

import com.san.kir.core.utils.viewModel.Action

internal sealed interface LocalItemsEvent : Action {
    data object Update : LocalItemsEvent
}
