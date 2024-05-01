package com.san.kir.features.shikimori.ui.search

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.base.ShikimoriManga

internal data class SearchState(
    val search: SearchingState = SearchingState.None,
) : ScreenState

internal sealed interface SearchingState {
    data object Load : SearchingState
    data object None : SearchingState
    data object Error : SearchingState
    data class Ok(val items: List<ShikimoriManga>) : SearchingState
}
