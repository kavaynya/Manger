package com.san.kir.features.shikimori.ui.search

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.shikimori.logic.di.profileItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

internal class SearchViewModel(
    private val profileItemRepository: ProfileItemRepository = ManualDI.profileItemRepository,
) : ViewModel<SearchState>(), SearchStateHolder {

    private var job: Job? = null
    private val searchingState = MutableStateFlow<SearchingState>(SearchingState.None)

    override val tempState = searchingState.map { search -> SearchState(search) }
    override val defaultState = SearchState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is SearchEvent.Search -> {
                job?.cancel()
                job = viewModelScope.defaultLaunch {
                    // Добавлена задержка поиска при вводе запроса
                    delay(1.seconds)
                    search(event.text)
                }
            }
        }
    }

    private suspend fun search(text: String) {
        searchingState.update { SearchingState.Load }
        profileItemRepository
            .search(text)
            .onSuccess { items ->
                searchingState.update {
                    if (items.isEmpty()) {
                        SearchingState.None
                    } else {
                        SearchingState.Ok(items)
                    }
                }
            }
            .onFailure {
                searchingState.update { SearchingState.Error }
                Timber.e(it)
            }

    }
}
