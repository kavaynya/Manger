package com.san.kir.features.accounts.shikimori.ui.search

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem

internal data class SearchState(
    val search: SearchingState = SearchingState.None,
) : ScreenState

internal sealed interface SearchingState {
    data object Load : SearchingState
    data object None : SearchingState
    data object Error : SearchingState
    data class Ok(val items: List<AccountMangaItem>) : SearchingState
}
