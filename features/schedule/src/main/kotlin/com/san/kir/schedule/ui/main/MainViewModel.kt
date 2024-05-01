package com.san.kir.schedule.ui.main

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.coroutines.flow.flowOf

internal class MainViewModel : ViewModel<MainState>(), MainStateHolder {
    override val tempState = flowOf(MainState())

    override val defaultState = MainState()

    override suspend fun onEvent(event: Action) {}
}
