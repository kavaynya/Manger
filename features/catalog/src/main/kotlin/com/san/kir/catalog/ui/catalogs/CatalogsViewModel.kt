package com.san.kir.catalog.ui.catalogs

import com.san.kir.background.logic.UpdateCatalogManager
import com.san.kir.background.logic.di.updateCatalogManager
import com.san.kir.catalog.logic.di.catalogRepository
import com.san.kir.catalog.logic.repo.CatalogRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class CatalogsViewModel(
    private val catalogRepository: CatalogRepository = ManualDI.catalogRepository,
    private val manager: UpdateCatalogManager = ManualDI.updateCatalogManager,
) : ViewModel<CatalogsState>(), CatalogsStateHolder {
    private var job: Job? = null
    private val items = MutableStateFlow(persistentListOf<CheckableSite>())
    private val background = MutableStateFlow(false)

    override val tempState = combine(items, background, ::CatalogsState)

    override val defaultState = CatalogsState(
        items = persistentListOf(),
        background = false
    )

    init {
        updateItemData()
    }

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            CatalogsEvent.UpdateData -> updateItemData()
            CatalogsEvent.UpdateContent -> {
                catalogRepository.items.forEach { manager.addTask(it.name) }
            }
        }
    }

    private fun updateItemData() {
        val temp = catalogRepository.items.map {
            it to CheckableSite(
                name = it.name,
                host = it.host,
                volume = VolumeState.Load,
                state = SiteState.Load
            )
        }

        items.update { temp.map { it.second }.toPersistentList() }
        setUpdateCatalogsListener()

        temp.forEachIndexed { index, (catalog, site) ->
            viewModelScope.defaultLaunch {
                val volume = kotlin.runCatching { catalog.init() }.getOrNull()?.volume
                items.update { list ->
                    val result =
                        if (volume != null) SiteState.Ok
                        else SiteState.Error
                    val currentSite = list[index]
                    list.set(index, currentSite.copy(state = result))
                }

                val dbVolume = catalogRepository.volume(catalog.name).getOrNull()
                items.update { list ->
                    val result =
                        if (dbVolume != null) VolumeState.Ok(
                            dbVolume, maxOf((volume ?: 0) - dbVolume, 0)
                        )
                        else VolumeState.Error

                    val currentSite = list[index]
                    list.set(index, currentSite.copy(volume = result))
                }
            }
        }
    }

    private fun setUpdateCatalogsListener() {
        job?.cancel()
        job = viewModelScope.defaultLaunch {
            manager.loadTasks()
                .collect { tasks ->
                    background.update { tasks.isNotEmpty() }
                }
        }
    }
}
