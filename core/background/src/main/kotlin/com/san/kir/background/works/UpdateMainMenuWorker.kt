package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkerParameters
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.mainMenuRepository
import com.san.kir.data.models.utils.MainMenuType

public class UpdateMainMenuWorker(
    private val ctx: Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    private val mainMenuRepository = ManualDI.mainMenuRepository()

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            checkNewItems()
            updateMenuItems()
            checkItemsForRemove()
        }.onFailure { return Result.retry() }
        return Result.success()
    }

    private suspend fun checkNewItems() {
        // Добавление новых
        val items = mainMenuRepository.items()
        MainMenuType.entries
            .filter { it.added }
            .filter { type ->
                items.none { it.type == type }
            }
            .forEach {
                mainMenuRepository.insert(
                    name = ctx.getString(it.stringId()), order = 100, type = it
                )
            }
    }

    private suspend fun updateMenuItems() {
        // Обновление старых
        mainMenuRepository.insert(mainMenuRepository
            .items()
            .map { item ->
                item.copy(name = ctx.getString(item.type.stringId()))
            }
        )
    }

    private suspend fun checkItemsForRemove() {
        // Удаление не нужных
        val notNeeded = MainMenuType.entries.filter { it.added.not() }
        if (notNeeded.isNotEmpty())
            mainMenuRepository.items().filter { type ->
                notNeeded.any { it == type.type }
            }.forEach {
                mainMenuRepository.delete(it)
            }
    }

    public companion object {
        private const val TAG = "updateMainMenu"

        public fun addTask(): Operation {
            val task = OneTimeWorkRequestBuilder<UpdateMainMenuWorker>()
                .addTag(TAG)
                .build()
            return ManualDI.workManager().enqueueUniqueWork(
                TAG + "Unique",
                ExistingWorkPolicy.KEEP,
                task
            )
        }
    }
}
