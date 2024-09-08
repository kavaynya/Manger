package com.san.kir.manger

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.SettingsRepository
import com.san.kir.data.settingsRepository
import kotlinx.coroutines.flow.map

class MainViewModel(
    settingsRepository: SettingsRepository = ManualDI.settingsRepository(),
) : ViewModel<MainState>(), MainStateHolder {
    override val tempState = settingsRepository.isDarkTheme.map { MainState(it) }
    override val defaultState = MainState()
}

interface MainStateHolder : StateHolder<MainState>

data class MainState(val isDarkTheme: Boolean = true) : ScreenState
