package com.san.kir.library.ui.library

import com.san.kir.background.logic.UpdateMangaManager
import com.san.kir.background.logic.di.updateMangaManager
import com.san.kir.background.util.collectWorkInfoByTag
import com.san.kir.background.works.AppUpdateWorker
import com.san.kir.background.works.MangaDeleteWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.extend.CategoryWithMangas
import com.san.kir.library.logic.di.mangaRepository
import com.san.kir.library.logic.di.settingsRepository
import com.san.kir.library.logic.repo.MangaRepository
import com.san.kir.library.logic.repo.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal class LibraryViewModel internal constructor(
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository,
    private val updateManager: UpdateMangaManager = ManualDI.updateMangaManager,
    settingsRepository: SettingsRepository = ManualDI.settingsRepository,
) : ViewModel<LibraryState>(), LibraryStateHolder {

    private val selectedMangaState =
        MutableStateFlow<SelectedMangaState>(SelectedMangaState.NonVisible)
    private val currentCategory = MutableStateFlow(CategoryWithMangas())
    private val backgroundState = MutableStateFlow<BackgroundState>(BackgroundState.None)

    init {
        checkWorks()
    }

    override val tempState = combine(
        selectedMangaState,
        currentCategory,
        mangaRepository.itemsState,
        settingsRepository.main().map { it.isShowCategory },
        backgroundState,
        ::LibraryState
    )

    override val defaultState = LibraryState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            LibraryEvent.NonSelect -> deSelectManga()

            is LibraryEvent.SelectManga ->
                selectedMangaState.update { SelectedMangaState.Visible(event.item) }

            is LibraryEvent.SetCurrentCategory -> currentCategory.update { event.item }

            is LibraryEvent.ChangeCategory -> {
                deSelectManga()
                mangaRepository.changeCategory(event.mangaId, event.categoryId)
            }

            is LibraryEvent.DeleteManga -> {
                deSelectManga()
                MangaDeleteWorker.addTask(event.mangaId, event.withFiles)
            }

            LibraryEvent.UpdateApp -> AppUpdateWorker.addTask()
            LibraryEvent.UpdateAll -> {
                state.value.apply {
                    if (items is ItemsState.Ok) updateManager.addTasks(
                        items.items.flatMap { it.mangas.map { m -> m.id } }
                    )
                }
            }

            LibraryEvent.UpdateCurrentCategory -> {
                updateManager.addTasks(currentCategory.value.mangas.map { it.id })
            }
        }
    }

    private fun deSelectManga() = selectedMangaState.update { SelectedMangaState.NonVisible }

    private fun checkWorks() {
        viewModelScope.defaultLaunch {
            collectWorkInfoByTag(MangaDeleteWorker.tag) { works ->
                if (works.all { it.state.isFinished }) backgroundState.update { BackgroundState.None }
                else backgroundState.update { BackgroundState.Work }
            }
        }
    }
}
