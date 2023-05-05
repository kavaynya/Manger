package com.san.kir.storage.ui.storages

import android.content.Context
import com.san.kir.background.util.collectWorkInfoByTag
import com.san.kir.background.works.StoragesUpdateWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.Storage
import com.san.kir.data.models.extend.MangaLogo
import com.san.kir.storage.logic.di.storageRepository
import com.san.kir.storage.logic.repo.StorageRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class StoragesViewModel(
    context: Context = ManualDI.context,
    private val storageRepository: StorageRepository = ManualDI.storageRepository,
) : ViewModel<StoragesState>(), StoragesStateHolder {
    private var job: Job? = null
    private val mangas = MutableStateFlow(persistentListOf<MangaLogo?>())
    private val backgroundState = MutableStateFlow<BackgroundState>(BackgroundState.Load)

    override val tempState = combine(
        storageRepository.items.findMangaForStorage(),
        mangas,
        backgroundState
    ) { items, mangas, background ->
        StoragesState(items.toPersistentList(), mangas, background)
    }

    override val defaultState = StoragesState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is StoragesEvent.Delete -> storageRepository.delete(event.item)
        }
    }

    init {
        StoragesUpdateWorker.runTask(context)

        viewModelScope.defaultLaunch {
            collectWorkInfoByTag(StoragesUpdateWorker.tag) { works ->
                if (works.isEmpty()) {
                    backgroundState.update { BackgroundState.None }
                } else {
                    if (works.all { it.state.isFinished }) {
                        backgroundState.update { BackgroundState.None }
                    } else {
                        backgroundState.update { BackgroundState.Load }
                    }
                }
            }
        }
    }

    private fun Flow<List<Storage>>.findMangaForStorage(): Flow<List<Storage>> =
        distinctUntilChanged()
            .onEach { items ->
                job?.cancel()
                job = viewModelScope.defaultLaunch {
                    items.forEachIndexed { index, storage ->
                        mangas.update { items ->
                            if (items.getOrNull(index) == null)
                                items.add(index, storageRepository.mangaFromPath(storage.path))
                            else
                                items
                        }
                    }
                }
            }
}
