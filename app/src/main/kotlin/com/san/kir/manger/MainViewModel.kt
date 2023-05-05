package com.san.kir.manger

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.manger.logic.di.settingsRepository
import com.san.kir.manger.logic.repo.SettingsRepository
import kotlinx.coroutines.flow.map

class MainViewModel constructor(
    settingsRepository: SettingsRepository = ManualDI.settingsRepository,
) : ViewModel<MainState>(), MainStateHolder {

    override val tempState = settingsRepository
        .main()
        .map { MainState(it.theme) }

    override val defaultState = MainState()

    override suspend fun onEvent(event: ScreenEvent) {
        TODO("Not yet implemented")
    }

}

interface MainStateHolder : StateHolder<MainState>

data class MainState(
    val theme: Boolean = true,
) : ScreenState
