package com.san.kir.storage.ui.storages

import com.san.kir.background.works.ClearStorageWorker
import com.san.kir.background.works.ClearStorageWorker.Companion.findId
import com.san.kir.background.works.StoragesUpdateWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.add
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.StorageRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.MangaLogo
import com.san.kir.data.models.main.Storage
import com.san.kir.data.storageRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class StoragesViewModel(
    private val storageRepository: StorageRepository = ManualDI.storageRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository()
) : ViewModel<StoragesState>(), StoragesStateHolder {

    private var job: Job? = null
    private val mangas = hashMapOf<Long, MangaLogo?>()
    private val backgroundState = MutableStateFlow(BackgroundState())
    private val deletableIds = MutableStateFlow(listOf<Long>())
    private val storageItems = MutableStateFlow(emptyList<StorageContainer>())

    override val items = combine(storageItems, deletableIds) { items, ids ->
        items.map { container -> container.copy(deleting = container.id in ids) }
    }.stateInSubscribed(emptyList())

    override val defaultState = StoragesState()

    override val tempState = combine(items, backgroundState) { items, background ->
        StoragesState(background, items.sumOf { it.storage.sizeFull }, items.size)
    }


    init {
        StoragesUpdateWorker.runTask()
        StoragesUpdateWorker.workInfos().onEach { works ->
            if (works.isEmpty()) {
                backgroundState.update { it.copy(load = false) }
            } else {
                if (works.all { it.state.isFinished }) {
                    backgroundState.update { it.copy(load = false) }
                } else {
                    backgroundState.update { it.copy(load = true) }
                }
            }
        }.launch()

        ClearStorageWorker.workInfos().onEach { works ->
            if (works.isEmpty()) {
                backgroundState.update { it.copy(deleting = 0) }
            } else {
                if (works.all { it.state.isFinished }) {
                    deletableIds.value = emptyList()
                    backgroundState.update { it.copy(deleting = 0) }
                } else {
                    backgroundState.update { it.copy(deleting = 1) }
                    works.filter { it.state.isFinished.not() }
                        .onEach { it.tags.findId()?.let { id -> deletableIds.update { old -> old.add(id) } } }
                }
            }
        }.launch()

        subscribeForItems()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is StoragesAction.Delete -> ClearStorageWorker.runTask(action.id)
        }
    }

    private fun subscribeForItems() {
        storageRepository.items.onEach { list ->
            this.storageItems.value = list.map { item -> StorageContainer(item, mangas[item.id]) }
            updateMangaLogo(list)
        }.launch()
    }

    private fun updateMangaLogo(list: List<Storage>) {
        job?.cancel()
        job = defaultLaunch {
            list.map { storage ->
                mangas.getOrPut(storage.id) { mangaRepository.itemByPath(storage.path) }
            }
            storageItems.update { old ->
                old.map { container -> StorageContainer(container.storage, mangas[container.storage.id]) }
            }
        }
    }
}
