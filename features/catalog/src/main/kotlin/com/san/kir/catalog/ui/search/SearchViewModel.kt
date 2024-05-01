package com.san.kir.catalog.ui.search

import android.content.Context
import com.san.kir.catalog.logic.di.catalogRepository
import com.san.kir.catalog.logic.repo.CatalogRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.addAll
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.coroutines.withMainContext
import com.san.kir.core.utils.longToast
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.extend.MiniCatalogItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal class SearchViewModel(
    private val context: Context = ManualDI.context,
    private val catalogRepository: CatalogRepository = ManualDI.catalogRepository,
) : ViewModel<SearchState>(), SearchStateHolder {
    private var job: Job? = null

    private val background = MutableStateFlow(false)
    private val search = MutableStateFlow("")
    private val items = MutableStateFlow(emptyList<MiniCatalogItem>())

    override val tempState = combine(items, search, background) { items, search, background ->
        SearchState(items.applyFilters(search), background)
    }

    override val defaultState = SearchState()

    init {
        loadItems()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is SearchEvent.Search -> updateFilter(action.query)
            is SearchEvent.UpdateManga -> updateManga(action.item)
        }
    }

    private fun loadItems() {
        background.update { true }
        defaultLaunch {
            catalogRepository.items.forEach { catalog ->
                items.update { old ->
                    old.addAll(catalogRepository.items(catalog.name))
                }
            }
            background.update { false }
        }
    }

    private fun updateFilter(value: String) {
        job?.cancel()
        job = launch {
            delay(1.seconds)
            search.update { value }
        }
    }

    private suspend fun updateManga(item: MiniCatalogItem) {
        catalogRepository.updateMangaBy(item)
        withMainContext {
            context.longToast("Информация о манге ${item.name} обновлена")
        }
    }

    private fun List<MiniCatalogItem>.applyFilters(query: String): List<MiniCatalogItem> {
        return if (query.isNotEmpty()) {
            filter { it.name.lowercase().contains(query.lowercase()) }
        } else this
    }
}
