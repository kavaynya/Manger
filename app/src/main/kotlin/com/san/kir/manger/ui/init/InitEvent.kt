package com.san.kir.manger.ui.init

import com.san.kir.core.utils.viewModel.ScreenEvent

sealed interface InitEvent : ScreenEvent {
    data class Next(val onSuccess: () -> Unit) : InitEvent
}
