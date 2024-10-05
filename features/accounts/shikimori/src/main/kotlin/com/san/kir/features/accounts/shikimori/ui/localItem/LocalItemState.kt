package com.san.kir.features.accounts.shikimori.ui.localItem

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriRate
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncState

internal data class LocalItemState(
    val manga: MangaState = MangaState.Load,
    val sync: SyncState = SyncState.Error,
    val profile: ProfileState = ProfileState.Load,
) : ScreenState

internal sealed interface MangaState {
    data class Ok(val item: LibraryMangaItem) : MangaState
    data object Load : MangaState
    data object Error : MangaState
}

internal sealed interface ProfileState {
    data class Ok(val rate: ShikimoriRate) : ProfileState
    data object None : ProfileState
    data object Load : ProfileState
}
