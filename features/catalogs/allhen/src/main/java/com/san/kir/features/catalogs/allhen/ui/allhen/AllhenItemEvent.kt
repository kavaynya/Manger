package com.san.kir.features.catalogs.allhen.ui.allhen

import com.san.kir.core.utils.viewModel.Action

internal sealed interface AllhenItemEvent : Action {
    data object Update : AllhenItemEvent
}
