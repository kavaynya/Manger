package com.san.kir.schedule.ui.schedule

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.coroutines.flow.flowOf

internal class ScheduleViewModel : ViewModel<ScheduleState>(), ScheduleStateHolder {
    override val tempState = flowOf(ScheduleState())

    override val defaultState = ScheduleState()

    override suspend fun onAction(action: Action) {}
}
