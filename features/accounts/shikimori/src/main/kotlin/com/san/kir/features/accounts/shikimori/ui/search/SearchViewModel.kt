package com.san.kir.features.accounts.shikimori.ui.search

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.accounts.shikimori.logic.di.accountItemRepository
import com.san.kir.features.accounts.shikimori.logic.models.Auth
import com.san.kir.features.accounts.shikimori.logic.models.toMangaItems
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
internal class SearchViewModel(
    private val accountId: Long,
    query: String,
    private val accountItemRepository: AccountItemRepository =
        ManualDI.accountItemRepository(accountId),
) : ViewModel<SearchState>(), SearchStateHolder {

    private val searchingState = MutableStateFlow<SearchingState>(SearchingState.None)
    private val query = MutableStateFlow(query)
    private val auth = accountItemRepository.authData.stateInEagerly(Auth())

    override val tempState = searchingState.map(::SearchState)
    override val defaultState = SearchState()

    init {
        this.query.debounce(2.seconds).onEach(::search).launch()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is SearchAction.Search -> query.value = action.text
        }
    }

    private suspend fun search(text: String) {
        searchingState.update { SearchingState.Load }
        accountItemRepository
            .search(text)
            .onSuccess { items ->
                searchingState.value =
                    if (items.isEmpty()) SearchingState.None
                    else SearchingState.Ok(items.toMangaItems(accountId))
            }
            .onFailure {
                searchingState.update { SearchingState.Error }
                Timber.e(it)
            }

    }
}
