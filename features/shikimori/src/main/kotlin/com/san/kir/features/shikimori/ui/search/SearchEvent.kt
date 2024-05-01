package com.san.kir.features.shikimori.ui.search

import com.san.kir.core.utils.viewModel.Action

internal sealed interface SearchEvent : Action {
    data class Search(val text: String) : SearchEvent
}
