package com.san.kir.catalog.ui.catalog

import android.content.Context
import com.san.kir.background.logic.UpdateCatalogManager
import com.san.kir.background.logic.di.updateCatalogManager
import com.san.kir.catalog.logic.linksForCatalog
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.coroutines.withMainContext
import com.san.kir.core.utils.longToast
import com.san.kir.core.utils.put
import com.san.kir.core.utils.remove
import com.san.kir.core.utils.set
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.catalogsRepository
import com.san.kir.data.db.catalog.repo.CatalogsRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.catalog.MiniCatalogItem
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class CatalogViewModel(
    catalogName: String,
    private val context: Context = ManualDI.application,
    private val catalogsRepository: CatalogsRepository = ManualDI.catalogsRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    private val siteCatalogManager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
    private val manager: UpdateCatalogManager = ManualDI.updateCatalogManager(),
) : ViewModel<CatalogState>(), CatalogStateHolder {
    private var job: Job? = null

    private var catalogItems = emptyList<MiniCatalogItem>()
    private val catalogName = siteCatalogManager.catalogName(catalogName)
    private val siteCatalog = siteCatalogManager.catalogByName(catalogName)

    override val filterState = MutableStateFlow(FilterState())
    override val sortState =
        MutableStateFlow(SortState(hasPopulateSort = siteCatalog.hasPopulateSort))
    override val filters = MutableStateFlow(emptyList<Filter>())
    override val backgroundWork = MutableStateFlow(BackgroundState())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val items = combine(
        filterState,
        sortState,
        mangaRepository.items
            .mapLatest { mangas ->
                if (catalogItems.isEmpty()) {
                    catalogItems = catalogsRepository.items(this.catalogName)
                }

                val mangaLinks = mangas.linksForCatalog(siteCatalog)

                catalogItems.map { item ->
                    if (mangaLinks.any { it.contains(item.shortLink) }) {
                        item.copy(state = MiniCatalogItem.State.Update)
                    } else {
                        item.copy(state = MiniCatalogItem.State.Added)
                    }
                }
            },
    ) { filterState, sortState, list ->
        backgroundWork.update { it.copy(updateItems = true) }

        if (filters.value.isEmpty()) {
            filters.update { list.initFilters() }
        }
        list.applyFilters(filterState).applySort(sortState)
    }.onEach {
        backgroundWork.update { it.copy(updateItems = false) }
    }.stateInSubscribed(emptyList())

    override val defaultState = CatalogState()
    override val tempState = MutableStateFlow(defaultState)

    init {
        manager.loadTask(this.catalogName)
            .onEach { task ->
                backgroundWork.update { old ->
                    if (task == null) {
                        old.copy(updateCatalogs = false, progress = null)
                    } else {
                        when (task.state) {
                            DownloadState.LOADING -> old.copy(
                                updateCatalogs = true, progress = task.progress,
                            )

                            DownloadState.QUEUED, DownloadState.PAUSED -> old.copy(
                                updateCatalogs = true, progress = null
                            )

                            else -> old
                        }
                    }
                }
            }
            .launchIn(this)
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is CatalogAction.UpdateManga -> updateManga(action.item)
            is CatalogAction.ChangeFilter -> changeFilter(action.type, action.index)
            is CatalogAction.Search -> filterState.update { it.copy(search = action.query) }
            is CatalogAction.ChangeSort -> sortState.update { it.copy(type = action.sort) }
            CatalogAction.Reverse -> sortState.update { it.copy(reverse = it.reverse.not()) }
            CatalogAction.ClearFilters -> clearFilters()
            CatalogAction.UpdateContent -> manager.addTask(catalogName)
            CatalogAction.CancelUpdateContent -> manager.removeTask(catalogName)
        }
    }

    private fun List<MiniCatalogItem>.initFilters(): List<Filter> {
        return listOf(
            Filter(FilterType.Genres, toSetSortedList { it.genres }),
            Filter(FilterType.Types, toSetSortedList { listOf(it.type) }),
            Filter(FilterType.Statuses, toSetSortedList { listOf(it.statusEdition) }),
            Filter(FilterType.Authors, toSetSortedList { it.authors })
        ).filter { it.items.size > 1 }
    }

    private fun List<MiniCatalogItem>.applyFilters(filter: FilterState): List<MiniCatalogItem> {
        var prepare = if (filter.search.isNotEmpty()) {
            filter { it.name.lowercase().contains(filter.search.lowercase()) }
        } else this

        filter.selectedFilters.forEach { (filter, items) ->
            prepare = when (filter) {
                FilterType.Authors -> prepare.filter { it.authors.containsAll(items) }
                FilterType.Genres -> prepare.filter { it.genres.containsAll(items) }
                FilterType.Statuses -> prepare.filter { items.contains(it.statusEdition) }
                FilterType.Types -> prepare.filter { items.contains(it.type) }
            }
        }

        return prepare
    }

    private fun List<MiniCatalogItem>.applySort(sort: SortState): List<MiniCatalogItem> {
        val sorted = when (sort.type) {
            SortType.Date -> sortedBy { it.dateId }
            SortType.Name -> sortedBy { it.name }
            SortType.Pop -> sortedBy { it.populate }
        }.sortedByDescending { it.state is MiniCatalogItem.State.Update }

        return (if (sort.reverse) sorted.reversed() else sorted)
    }

    private fun changeFilter(type: FilterType, index: Int) {
        defaultLaunch {
            val old = filters.value
            val indexFilter = old.indexOfFirst { it.type == type }

            if (indexFilter != -1) {
                val filter = old[indexFilter]
                val item = filter.items[index].run { copy(state = state.not()) }
                filters.update {
                    old.set(indexFilter, filter.copy(items = filter.items.set(index, item)))
                }
                filterState.update {
                    it.copy(
                        selectedFilters = it.selectedFilters.addOrRemoveSelectedFilter(
                            type,
                            item
                        )
                    )
                }
            }
        }
    }

    private fun Map<FilterType, List<String>>.addOrRemoveSelectedFilter(
        type: FilterType, item: SelectableItem,
    ): Map<FilterType, List<String>> {
        val oldItems = get(type)
        return if (oldItems == null) {
            put(type, if (item.state) listOf(item.name) else listOf())
        } else {
            val newItems = if (item.state) oldItems + item.name else oldItems - item.name
            if (newItems.isEmpty())
                remove(type)
            else
                put(type, newItems)
        }
    }

    private fun clearFilters() {
        filterState.update { it.copy(selectedFilters = emptyMap()) }
        filters.update { old ->
            old.map { filter ->
                filter.copy(items = filter.items.map { it.copy(state = false) })
            }
        }
    }

    private suspend fun updateManga(item: MiniCatalogItem) {
        val element = catalogsRepository.item(catalogName, item.id) ?: return
        val fullElement = siteCatalogManager.fullElement(element)
        mangaRepository.updateMangaBy(fullElement)

        withMainContext {
            context.longToast("Информация о манге ${item.name} обновлена")
        }
    }

    private fun List<MiniCatalogItem>.toSetSortedList(transform: (MiniCatalogItem) -> Iterable<String>): List<SelectableItem> {
        return flatMap { transform(it) }
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toHashSet()
            .sorted()
            .map { SelectableItem(it, false) }.toList()
    }
}
