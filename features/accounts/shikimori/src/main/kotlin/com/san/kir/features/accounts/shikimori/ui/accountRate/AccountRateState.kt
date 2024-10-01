package com.san.kir.features.accounts.shikimori.ui.accountRate

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncState

internal data class AccountRateState(
    val sync: SyncState = SyncState.Error,
    val item: AccountMangaItem = AccountMangaItem(),
    val itemState: ItemState = ItemState.Ok,
    val hasLoading: Boolean = false,
) : ScreenState

internal sealed interface ItemState {
    data object Ok : ItemState
    data object Error : ItemState
}
