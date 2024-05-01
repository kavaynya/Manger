package com.san.kir.features.catalogs.allhen.ui.comx

import com.san.kir.core.utils.viewModel.Action

internal sealed interface ComxItemEvent : Action {
    data object Update : ComxItemEvent
}
