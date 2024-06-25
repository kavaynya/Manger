package com.san.kir.schedule.ui.tasks

import com.san.kir.core.utils.viewModel.Action


internal sealed interface TasksAction : Action {
    data class Update(val itemId: Long, val state: Boolean) : TasksAction
}
