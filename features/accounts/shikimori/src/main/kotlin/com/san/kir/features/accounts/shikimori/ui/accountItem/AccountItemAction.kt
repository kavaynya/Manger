package com.san.kir.features.accounts.shikimori.ui.accountItem

import com.san.kir.core.utils.viewModel.Action

internal sealed interface AccountItemAction : Action {
    data object LogIn : AccountItemAction
    data class LogOut(val full: Boolean) : AccountItemAction
    data object Update : AccountItemAction
}
