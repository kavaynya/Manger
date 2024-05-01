package com.san.kir.manger.ui.init

import com.san.kir.core.utils.viewModel.Action

sealed interface InitEvent : Action {
    data class Next(val onSuccess: () -> Unit) : InitEvent
}
