package com.san.kir.schedule.ui.task

import android.content.Context
import androidx.work.WorkInfo
import com.san.kir.background.util.collectWorkInfoByTag
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.PlannedTask
import com.san.kir.data.models.base.toBase
import com.san.kir.schedule.logic.repo.TasksRepository
import com.san.kir.schedule.logic.repo.tasksRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class TaskViewModel(
    private val context: Context = ManualDI.context,
    private val tasksRepository: TasksRepository = ManualDI.tasksRepository,
) : ViewModel<TaskState>(), TaskStateHolder {
    private val item = MutableStateFlow(PlannedTask())
    private val hasChanges = MutableStateFlow(false)
    private val hasWork = MutableStateFlow(false)
    private var isNew = false

    override val tempState = combine(
        item,
        tasksRepository.categories,
        tasksRepository.mangas,
        hasChanges,
        hasWork
    ) { item, categories, mangas, changes, work ->

        val newItem = item.copy(
            mangas = item.mangas.ifEmpty { item.groupContent.mapNotNull { name -> mangas.firstOrNull { it.name == name }?.id } }
        )

        val action =
            if (changes) AvailableAction.Save
            else if (work.not()) AvailableAction.Start
            else AvailableAction.None

        TaskState(
            item = newItem,
            categoryName = categories.firstOrNull { it.id == newItem.categoryId }?.name
                ?: "",
            mangaName = mangas.firstOrNull { it.id == newItem.mangaId }?.name ?: "",
            categoryIds = categories.map { it.id }.toImmutableList(),
            categoryNames = categories.map { it.name }.toImmutableList(),
            mangaIds = mangas.map { it.id }.toImmutableList(),
            mangaNames = mangas.map { it.name }.toImmutableList(),
            catalogNames = tasksRepository.catalogs.toImmutableList(),
            groupNames = newItem.mangas.map { id -> mangas.first { it.id == id }.name }
                .toImmutableList(),
            availableAction = action,
        )
    }

    override val defaultState = TaskState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is TaskEvent.Set -> set(event.itemId)
            is TaskEvent.Change -> change(event.type)
            TaskEvent.Save -> save()
            TaskEvent.Start -> ScheduleWorker.addTaskNow(
                context, item.value.toBase(state.value.mangaName, state.value.categoryName)
            )
        }
    }

    private suspend fun set(taskId: Long) {
        item.update { tasksRepository.item(taskId) ?: it }
        isNew = taskId == -1L
        collectWorkInfoByTag(ScheduleWorker.tag + taskId) { works ->
            hasWork.value = works.any { it.state == WorkInfo.State.RUNNING }
        }
    }

    private suspend fun save() {
        if (isNew) {
            tasksRepository.save(item.value.copy(addedTime = System.currentTimeMillis()), true)
        } else {
            tasksRepository.save(item.value.copy(isEnabled = false), false)
            ScheduleWorker.cancelTask(
                context, item.value.toBase(state.value.mangaName, state.value.categoryName)
            )
        }
        hasChanges.value = false
    }

    private fun change(type: ChangeType) {
        item.update {
            when (type) {
                is ChangeType.Catalog -> it.copy(catalog = type.name)
                is ChangeType.Category -> it.copy(categoryId = type.categoryId)
                is ChangeType.Day -> it.copy(dayOfWeek = type.day)
                is ChangeType.Group -> it.copy(groupName = type.name)
                is ChangeType.Manga -> it.copy(mangaId = type.mangaId)
                is ChangeType.Mangas -> it.copy(mangas = type.mangaIds)
                is ChangeType.Period -> it.copy(period = type.period)
                is ChangeType.Time -> it.copy(hour = type.hour, minute = type.minute)
                is ChangeType.Type -> it.copy(type = type.type)
            }
        }
        hasChanges.value = true
    }
}
