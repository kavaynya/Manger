package com.san.kir.schedule.ui.tasks

import android.content.Context
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.support.PlannedPeriod
import com.san.kir.core.support.PlannedType
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.extend.SimplifiedTask
import com.san.kir.schedule.R
import com.san.kir.schedule.logic.repo.TasksRepository
import com.san.kir.schedule.logic.repo.tasksRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class TasksViewModel(
    private val context: Context = ManualDI.context,
    private val tasksRepository: TasksRepository = ManualDI.tasksRepository,
) : ViewModel<TasksState>(), TasksStateHolder {
    override val tempState = tasksRepository.items.map(transform())

    override val defaultState = TasksState(persistentListOf())

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is TasksEvent.Update -> {
                tasksRepository.update(event.itemId, event.state)
                val item = tasksRepository.items.first().first { it.id == event.itemId }
                if (event.state)
                    ScheduleWorker.addTask(context, item)
                else
                    ScheduleWorker.cancelTask(context, item)
            }
        }
    }

    private fun transform(): suspend (value: List<SimplifiedTask>) -> TasksState =
        { items ->
            TasksState(
                items
                    .map { Task(it.id, itemName(it), itemInfo(it), it.isEnabled) }
                    .toPersistentList()
            )
        }

    private fun itemName(item: SimplifiedTask) = when (item.type) {
        PlannedType.MANGA    -> context.getString(R.string.planned_task_name_manga, item.manga)
        PlannedType.GROUP    -> context.getString(R.string.planned_task_name_group, item.groupName)
        PlannedType.CATALOG  -> context.getString(R.string.planned_task_name_catalog, item.catalog)
        PlannedType.APP      -> context.getString(R.string.planned_task_name_app)
        PlannedType.CATEGORY -> context.getString(
            R.string.planned_task_name_category,
            item.category
        )
    }

    private fun itemInfo(item: SimplifiedTask): String {
        val dayText = context.getString(
            if (item.period == PlannedPeriod.DAY) item.period.dayText
            else item.dayOfWeek.dayText
        )
        return context.getString(
            R.string.planned_task_update_text_template,
            dayText,
            item.hour,
            String.format("%02d", item.minute)
        )
    }
}
