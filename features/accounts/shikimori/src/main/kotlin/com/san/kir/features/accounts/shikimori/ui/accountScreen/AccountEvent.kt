package com.san.kir.features.accounts.shikimori.ui.accountScreen

import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.utils.viewModel.Event
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem

internal interface AccountEvent : Event {
    data object ToBack : AccountEvent
    data object ToSearch : AccountEvent
    data class ToItem(val item: AccountMangaItem, val params: SharedParams) : AccountEvent
    data object ShowLogOutDialog : AccountEvent
}
