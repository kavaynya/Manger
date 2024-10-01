package com.san.kir.features.accounts.shikimori.ui.accountRate

import com.san.kir.core.utils.viewModel.Event
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem

internal sealed interface AccountRateEvent : Event {
    data class ShowChangeDialog(val item: AccountMangaItem) : AccountRateEvent
    data class ToSearch(val query: String) : AccountRateEvent
}
