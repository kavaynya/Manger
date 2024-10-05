package com.san.kir.features.accounts.shikimori.ui.search

import com.san.kir.core.utils.viewModel.Action

internal sealed interface SearchAction : Action {
    data class Search(val text: String) : SearchAction
}
