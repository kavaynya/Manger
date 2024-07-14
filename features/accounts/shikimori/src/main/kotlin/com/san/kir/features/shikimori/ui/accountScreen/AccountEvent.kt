package com.san.kir.features.shikimori.ui.accountScreen

import com.san.kir.core.utils.viewModel.Action

internal sealed interface AccountEvent : Action {
    data object LogOut : AccountEvent
    data object CancelLogOut : AccountEvent
    data object Update : AccountEvent
}
