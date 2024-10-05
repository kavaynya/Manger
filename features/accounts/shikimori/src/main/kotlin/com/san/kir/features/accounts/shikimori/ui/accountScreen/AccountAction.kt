package com.san.kir.features.accounts.shikimori.ui.accountScreen

import com.san.kir.core.utils.viewModel.Action


internal sealed interface AccountAction : Action {
    data class LogOut(val full: Boolean) : AccountAction
    data object Update : AccountAction
}
