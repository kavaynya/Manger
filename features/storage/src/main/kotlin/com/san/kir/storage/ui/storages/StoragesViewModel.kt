package com.san.kir.storage.ui.storages

import android.content.Context
import com.san.kir.background.works.StoragesUpdateWorker
import com.san.kir.core.utils.ManualDI
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class StoragesViewModel(
    context: Context = ManualDI.application,
    private val storageRepository: StorageRepository = ManualDI.storageRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository()
) : ViewModel<StoragesState>(), StoragesStateHolder {

    private var job: Job? = null
    private val mangas = hashMapOf<Long, MangaLogo?>()
    private val backgroundState = MutableStateFlow(BackgroundState())

    override val items = MutableStateFlow(emptyList<StorageContainer>())

    override val defaultState = StoragesState()

    override val tempState = combine(items, backgroundState) { items, background ->
        StoragesState(background, items.sumOf { it.storage.sizeFull }, items.size)
    }


    init {
        StoragesUpdateWorker.runTask()
        StoragesUpdateWorker.workInfos()
            .onEach { works ->
                if (works.isEmpty()) {
                    backgroundState.update { it.copy(load = false) }
                } else {
                    if (works.all { it.state.isFinished }) {
                        backgroundState.update { it.copy(load = false) }
                    } else {
                        backgroundState.update { it.copy(load = true) }
                    }
                }
            }
            .launchIn(this)

        subscribeForItems()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is StoragesAction.Delete -> delete(action.item)
        }
    }

    private fun subscribeForItems() {
        storageRepository
            .items
            .onEach { list ->
                this.items.value = list.map { item -> StorageContainer(item, mangas[item.id]) }
                updateMangaLogo(list)
            }
            .launchIn(this)
    }

    private fun updateMangaLogo(list: List<Storage>) {
        job?.cancel()
        job = defaultLaunch {
            list.map { storage ->
                val logo = mangas.getOrPut(storage.id) {
                    mangaRepository.itemByPath(storage.path)
                }

            }
            items.update { old ->
                old.map { container ->
                    StorageContainer(container.storage, mangas[container.storage.id])
                }
            }
        }
    }

    private suspend fun delete(item: Storage) {
        backgroundState.update { it.copy(load = false, deleting = it.deleting + 1) }
        storageRepository.delete(item)
        backgroundState.update { it.copy(load = false, deleting = it.deleting - 1) }
    }
}
