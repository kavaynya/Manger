package com.san.kir.catalog.ui.search

import android.content.Context
import com.san.kir.catalog.logic.linksForCatalog
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withMainContext
import com.san.kir.core.utils.longToast
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.catalogsRepository
import com.san.kir.data.db.catalog.repo.CatalogsRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.catalog.MiniCatalogItem
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds


internal class SearchViewModel(
    private val context: Context = ManualDI.application,
    private val catalogsRepository: CatalogsRepository = ManualDI.catalogsRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
) : ViewModel<SearchState>(), SearchStateHolder {
    private val allItems = manager.catalog.associate { it.catalogName to items(it.catalogName) }
    private val search = MutableStateFlow("")
    private val background = MutableStateFlow(false)
    private val previousFilterState = MutableStateFlow(FilterState())
    private val currentFilterState =
        MutableStateFlow(FilterState(manager.catalog.map { it.catalogName }))

    @OptIn(ExperimentalCoroutinesApi::class)
    private val catalogs = currentFilterState.mapLatest { (selected) ->
        manager.catalog.map {
            SelectableCatalog(it.name, it.catalogName, it.catalogName in selected)
        }
    }

    private val hasFilterChanges =
        combine(currentFilterState, previousFilterState, FilterState::hasChanges)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val items = previousFilterState
        .onEach { background.value = true }
        .transformLatest { (catalogs, added) ->
            val items =
                allItems.filterKeys { name -> catalogs.contains(name) }.values.toTypedArray()

            emitAll(combine(*items) { list ->
                val handledItems = list.flatMap { it }
                    .sortedByDescending { it.state is MiniCatalogItem.State.Update }
                if (added) handledItems
                else handledItems.filter { it.state is MiniCatalogItem.State.Update }
            })
        }
        .combine(search.debounce(1.seconds)) { list, str ->
            background.value = true
            list.applyFilters(str)
        }
        .onEach { background.value = false }
        .stateInSubscribed(emptyList())

    override val tempState =
        combine(
            background, catalogs, hasFilterChanges, currentFilterState
        ) { background, catalogs, hasChanges, filter ->
            SearchState(background, catalogs, hasChanges, filter.addedMangaVisible)
        }

    override val defaultState = SearchState()

    init {
        applyCatalogFilter()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is SearchAction.Search -> updateFilter(action.query)
            is SearchAction.UpdateManga -> updateManga(action.item)
            is SearchAction.ChangeCatalogSelect -> updateCatalogSelect(action.name)
            is SearchAction.ChangeAddMangaVisible -> updateAddedMangaVisible(action.state)
            is SearchAction.ApplyCatalogFilter -> applyCatalogFilter()
        }
    }

    private fun updateFilter(value: String) = search.update { value }

    private suspend fun updateManga(item: MiniCatalogItem) {
        val dbItem =
            catalogsRepository.item(manager.catalogName(item.catalogName), item.id) ?: return

        val fullElement = manager.fullElement(dbItem)
        mangaRepository.updateMangaBy(fullElement)

        withMainContext {
            context.longToast("Информация о манге ${item.name} обновлена")
        }
    }

    private fun updateCatalogSelect(name: String) {
        currentFilterState.update { old ->
            val selected = if (name in old.selectedFilters) old.selectedFilters - name
            else old.selectedFilters + name
            old.copy(selectedFilters = selected)
        }
    }

    private fun updateAddedMangaVisible(state: Boolean) {
        currentFilterState.update { old -> old.copy(addedMangaVisible = state) }
    }

    private fun applyCatalogFilter() {
        previousFilterState.value = currentFilterState.value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun items(catalogName: String): Flow<List<MiniCatalogItem>> {
        Timber.w("items($catalogName)")
        val catalogName = manager.catalogName(catalogName)
        Timber.w("manager.catalogName($catalogName)")

        return mangaRepository.items.mapLatest { mangas ->
            val items = catalogsRepository.items(catalogName)
            val links = mangas.linksForCatalog(manager.catalogByName(catalogName))
            items.onEach { item ->
                val state = if (links.any { item.shortLink in it }) {
                    MiniCatalogItem.State.Update
                } else {
                    MiniCatalogItem.State.Added
                }
                item.state = state
            }
        }
    }

    private fun List<MiniCatalogItem>.applyFilters(query: String): List<MiniCatalogItem> {
        return if (query.isNotEmpty()) {
            filter { query.lowercase() in it.name.lowercase() }
        } else this
    }
}
