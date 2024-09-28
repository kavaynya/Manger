package com.san.kir.features.accounts.shikimori.ui.accountItem

import com.san.kir.core.utils.viewModel.Event

internal sealed interface AccountItemEvent : Event {
    data class LogOut(val full: Boolean) : AccountItemEvent

    data object ShowLogoutDialog : AccountItemEvent
}
