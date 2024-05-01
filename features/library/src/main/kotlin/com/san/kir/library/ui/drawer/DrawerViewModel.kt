package com.san.kir.library.ui.drawer

import android.content.Context
import com.san.kir.background.works.UpdateMainMenuWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.library.logic.di.mainMenuRepository
import com.san.kir.library.logic.di.settingsRepository
import com.san.kir.library.logic.repo.MainMenuRepository
import com.san.kir.library.logic.repo.SettingsRepository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
internal class DrawerViewModel(
    private val context: Context = ManualDI.context,
    settingsRepository: SettingsRepository = ManualDI.settingsRepository,
    private val mainMenuRepository: MainMenuRepository = ManualDI.mainMenuRepository,
) : ViewModel<DrawerState>(), DrawerStateHolder {

    override val tempState = combine(
        settingsRepository.main().mapLatest { it.editMenu },
        mainMenuRepository.items.onStart { UpdateMainMenuWorker.addTask(context) }
    ) { edit, menu ->
        DrawerState(edit, MainMenuItemsState.Ok(menu))
    }

    override val defaultState = DrawerState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is DrawerEvent.Reorder -> mainMenuRepository.swap(event.from, event.to)
        }
    }
}
