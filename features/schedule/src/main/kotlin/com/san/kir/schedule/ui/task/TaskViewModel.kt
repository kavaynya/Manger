package com.san.kir.schedule.ui.task

import androidx.work.WorkInfo
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.categoryRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.PlannedRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.PlannedTask
import com.san.kir.data.models.main.toBase
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.plannedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class TaskViewModel(
    private val itemId: Long,
    private val plannedRepository: PlannedRepository = ManualDI.plannedRepository(),
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
) : ViewModel<TaskState>(), TaskStateHolder {
    private val item = MutableStateFlow(PlannedTask())
    private val editedItem = MutableStateFlow(PlannedTask())
    private val hasChanges = combine(item, editedItem) { old, new -> old != new }
    private val hasWork = MutableStateFlow(BackgroundWork())

    override val tempState = combine(
        editedItem, categoryRepository.namesAndIds, mangaRepository.namesAndIds, hasChanges, hasWork
    ) { item, categories, mangas, changes, work ->

        val newItem = item.copy(
            mangas = item.mangas.ifEmpty {
                item.groupContent.mapNotNull { name -> mangas.firstOrNull { it.name == name }?.id }
            }
        )

        val action = when {
            changes -> AvailableAction.Save
            work.hasBackgrounds.not() && this.item.value.isConfigured -> AvailableAction.Start
            else -> AvailableAction.None
        }

        TaskState(
            item = newItem,
            categoryName = categories.firstOrNull { it.id == newItem.categoryId }?.name ?: "",
            mangaName = mangas.firstOrNull { it.id == newItem.mangaId }?.name ?: "",
            categoryIds = categories.map { it.id },
            categoryNames = categories.map { it.name },
            mangaIds = mangas.map { it.id },
            mangaNames = mangas.map { it.name },
            catalogNames = manager.catalog.map { it.name },
            groupNames = newItem.mangas.map { id -> mangas.first { it.id == id }.name },
            availableAction = action,
        )
    }

    override val defaultState = TaskState()

    init {
        defaultLaunch {
            hasWork.update { it.copy(hasAction = true) }

            val dbItem = plannedRepository.item(itemId) ?: PlannedTask()

            item.value = dbItem
            editedItem.value = dbItem

            hasWork.update { it.copy(hasAction = false) }
        }

        ScheduleWorker.workInfos(itemId)
            .onEach { works ->
                hasWork.update { old ->
                    val hasWork = works.any { it.state == WorkInfo.State.RUNNING }
                    old.copy(hasWork = hasWork)
                }
            }
            .launch()
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            is TaskAction.Change -> change(action.type)
            TaskAction.Save -> save()
            TaskAction.Restore -> restore()
            TaskAction.Start -> ScheduleWorker.addTaskNow(
                item.value.toBase(state.value.mangaName, state.value.categoryName)
            )
        }
    }

    private suspend fun save() {
        var saveItem = editedItem.value
        if (saveItem.isNew) {
            val ids = plannedRepository.save(saveItem.copy(addedTime = System.currentTimeMillis()))
            saveItem = saveItem.copy(id = ids.first())
            item.value = saveItem
            editedItem.value = saveItem
        } else {
            plannedRepository.save(saveItem.copy(isEnabled = false))
            item.value = saveItem
            ScheduleWorker.cancelTask(saveItem.toBase(state.value.mangaName, state.value.categoryName))
        }
    }

    private fun restore() {
        editedItem.value = item.value
    }

    private fun change(type: ChangeType) {
        editedItem.update {
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
    }
}
