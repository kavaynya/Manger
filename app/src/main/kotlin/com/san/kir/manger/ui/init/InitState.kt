package com.san.kir.manger.ui.init

import com.san.kir.core.utils.viewModel.ScreenState

sealed interface InitState : ScreenState {
    data object Memory : InitState
    data object Notification : InitState
    data object Init : InitState
}
