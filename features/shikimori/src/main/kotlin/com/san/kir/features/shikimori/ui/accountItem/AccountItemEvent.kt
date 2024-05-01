package com.san.kir.features.shikimori.ui.accountItem

import com.san.kir.core.utils.viewModel.Action

internal sealed interface AccountItemEvent : Action {
    data object LogIn : AccountItemEvent
    data object LogOut : AccountItemEvent
    data object CancelLogOut : AccountItemEvent
}
