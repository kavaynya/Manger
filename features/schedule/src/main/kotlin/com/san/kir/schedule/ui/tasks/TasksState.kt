package com.san.kir.schedule.ui.tasks

import com.san.kir.core.utils.viewModel.ScreenState



internal data class TasksState(
    val items: List<Task>
) : ScreenState

internal data class Task(
    val id: Long,
    val name: String,
    val info: String,
    val isEnabled: Boolean
)
