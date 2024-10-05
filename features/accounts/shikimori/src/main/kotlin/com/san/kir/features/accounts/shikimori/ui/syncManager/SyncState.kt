package com.san.kir.features.accounts.shikimori.ui.syncManager

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem

internal sealed interface SyncState : ScreenState {
    data object Finding : SyncState
    data object Error : SyncState
    data object Empty : SyncState
    data class Ok(val manga: MangaItem) : SyncState
    data class Founds(val items: List<SyncItemState>) : SyncState
    data class NotFound(val name: String) : SyncState
}
