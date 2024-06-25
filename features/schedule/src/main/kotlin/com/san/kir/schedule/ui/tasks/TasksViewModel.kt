package com.san.kir.schedule.ui.tasks

import android.content.Context
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.PlannedRepository
import com.san.kir.data.models.main.SimplifiedTask
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.plannedRepository
import com.san.kir.schedule.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class TasksViewModel(
    private val context: Context = ManualDI.application,
    private val tasksRepository: PlannedRepository = ManualDI.plannedRepository(),
) : ViewModel<TasksState>(), TasksStateHolder {
    override val tempState = tasksRepository.simplifiedItems.map(transform())
    override val defaultState = TasksState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is TasksAction.Update -> update(action.itemId, action.state)
        }
    }

    private fun transform(): suspend (value: List<SimplifiedTask>) -> TasksState =
        { items -> TasksState(items.map { Task(it.id, itemName(it), itemInfo(it), it.isEnabled) }) }

    private fun itemName(item: SimplifiedTask) = when (item.type) {
        PlannedType.MANGA -> context.getString(R.string.manga_format, item.manga)
        PlannedType.GROUP -> context.getString(R.string.group_format, item.groupName)
        PlannedType.CATALOG -> context.getString(R.string.catalog_format, item.catalog)
        PlannedType.APP -> context.getString(R.string.application)
        PlannedType.CATEGORY -> context.getString(R.string.category_format, item.category)
    }

    private fun itemInfo(item: SimplifiedTask): String {
        val dayText = context.getString(
            if (item.period == PlannedPeriod.DAY) item.period.dayText else item.dayOfWeek.dayText
        )
        return context.getString(
            R.string.updating_format,
            dayText,
            item.hour,
            String.format("%02d", item.minute)
        )
    }

    private suspend fun update(itemId: Long, state: Boolean) {
        tasksRepository.update(itemId, state)
        val item = tasksRepository.simplifiedItems.first().first { it.id == itemId }
        if (state) ScheduleWorker.addTask(item)
        else ScheduleWorker.cancelTask(item)
    }
}
