package com.san.kir.background.works

import android.app.AlarmManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.R
import com.san.kir.background.logic.di.updateCatalogManager
import com.san.kir.background.logic.di.updateMangaManager
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.longToast
import com.san.kir.data.categoryRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.base.PlannedTaskBase
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.plannedRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

public class ScheduleWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val plannedRepository = ManualDI.plannedRepository()
    private val mangaRepository = ManualDI.mangaRepository()
    private val categoriesRepository = ManualDI.categoryRepository()
    private val updateCatalogManager = ManualDI.updateCatalogManager()
    private val updateMangaManager = ManualDI.updateMangaManager()

    override suspend fun doWork(): Result {
        val id = inputData.getLong("planned_task", -1L)
        if (id == -1L) return Result.retry()

        kotlin.runCatching {
            val task = plannedRepository.item(id) ?: return Result.failure()
            Timber.v("task $task")

            when (task.type) {
                PlannedType.MANGA -> {
                    val manga = mangaRepository.item(task.mangaId)
                    if (manga != null) {
                        updateMangaManager.addTask(manga.id)
                    } else {
                        Timber.tag("ScheduleWorker").e("Не найдена манга с id -> ${task.mangaId}")
                    }
                }

                PlannedType.GROUP -> {
                    if (task.groupContent.isNotEmpty())
                        updateMangaManager.addTasks(mangaRepository.idsByNames(task.groupContent))
                    else if (task.mangas.isNotEmpty())
                        updateMangaManager.addTasks(task.mangas)
                    else Unit
                }

                PlannedType.CATEGORY -> {
                    val defaultCategory = categoriesRepository.defaultCategory()

                    val mangas =
                        if (defaultCategory.id == task.categoryId) mangaRepository.ids()
                        else mangaRepository.idsByCategoryId(task.categoryId)
                    updateMangaManager.addTasks(mangas)
                }

                PlannedType.CATALOG -> updateCatalogManager.addTask(task.catalog)
                PlannedType.APP -> AppUpdateWorker.addTask()
            }
        }.fold(
            onSuccess = { return Result.success() },
            onFailure = {
                it.printStackTrace()
                return Result.failure()
            }
        )
    }

    public companion object {
        private const val TAG = "scheduleWork"
        private const val DAY_PERIOD = AlarmManager.INTERVAL_DAY
        private const val WEEK_PERIOD = DAY_PERIOD * 7

        public fun addTaskNow(item: PlannedTaskBase) {
            val oneTask = OneTimeWorkRequestBuilder<ScheduleWorker>()
                .addTag(TAG + item.id)
                .setInputData(workDataOf("planned_task" to item.id))
                .build()
            ManualDI.workManager().enqueue(oneTask)
        }

        public fun addTask(item: PlannedTaskBase) {
            val delay = getDelay(item)
            Timber.v("delay $delay ")

            ManualDI.workManager()
                .cancelAllWorkByTag(TAG + item.id)
                .result
                .addListener(
                    {
                        val perTask = PeriodicWorkRequestBuilder<ScheduleWorker>(
                            if (item.period == PlannedPeriod.DAY) 1L else 7L, TimeUnit.DAYS,
                            1L, TimeUnit.MINUTES,
                        )
                            .addTag(TAG + item.id)
                            .setInputData(workDataOf("planned_task" to item.id))
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .build()

                        val oneTask = OneTimeWorkRequestBuilder<ScheduleWorker>()
                            .addTag(TAG + item.id)
                            .setInputData(workDataOf("planned_task" to item.id))
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .build()

                        ManualDI.workManager().enqueue(listOf(oneTask, perTask))
                    }, ContextCompat.getMainExecutor(ManualDI.application)
                )
        }

        public fun cancelTask(item: PlannedTaskBase) {
            ManualDI.workManager()
                .cancelAllWorkByTag(TAG + item.id)
                .result
                .addListener(
                    {
                        when (item.type) {
                            PlannedType.MANGA ->
                                ManualDI.application.longToast(
                                    R.string.manga_updating_was_canceled_format,
                                    item.manga
                                )

                            PlannedType.CATEGORY ->
                                ManualDI.application.longToast(
                                    R.string.category_updating_was_canceled_format,
                                    item.category
                                )

                            PlannedType.GROUP ->
                                ManualDI.application.longToast(
                                    R.string.group_updating_was_canceled_format,
                                    item.groupName
                                )

                            PlannedType.CATALOG ->
                                ManualDI.application.longToast(
                                    R.string.catalog_updating_was_canceled_format,
                                    item.catalog
                                )

                            PlannedType.APP ->
                                ManualDI.application.longToast(R.string.app_updating_was_canceled)
                        }
                    }, ContextCompat.getMainExecutor(ManualDI.application)
                )

        }

        public fun workInfos(itemId: Long): Flow<List<WorkInfo>> = WorkManager
            .getInstance(ManualDI.application)
            .getWorkInfosByTagFlow(TAG + itemId)

        private fun getDelay(item: PlannedTaskBase): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, item.hour)
            calendar.set(Calendar.MINUTE, item.minute)
            calendar.set(Calendar.SECOND, 0)
            if (item.period == PlannedPeriod.WEEK) {
                calendar.set(Calendar.DAY_OF_WEEK, item.dayOfWeek.order)
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.timeInMillis += WEEK_PERIOD
                }
            }

            return if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.timeInMillis + DAY_PERIOD - System.currentTimeMillis()
            } else {
                calendar.timeInMillis - System.currentTimeMillis()
            }
        }

    }
}

