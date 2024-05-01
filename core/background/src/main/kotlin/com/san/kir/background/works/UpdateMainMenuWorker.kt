package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.db.main.dao.MainMenuDao
import com.san.kir.data.mainMenuDao
import com.san.kir.data.db.main.entites.DbMainMenuItem
import com.san.kir.data.models.utils.MainMenuType

class UpdateMainMenuWorker(
    private val ctx: Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    private val mainMenuDao: MainMenuDao = ManualDI.mainMenuDao

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
        val items = mainMenuDao.items()
        MainMenuType.values()
            .filter { it.added }
            .filter { type ->
                items.none { it.type == type }
            }
            .forEach {
                mainMenuDao.insert(
                    DbMainMenuItem(name = ctx.getString(it.stringId()), order = 100, type = it)
                )
            }
    }

    private suspend fun updateMenuItems() {
        // Обновление старых
        mainMenuDao.update(*mainMenuDao
            .items()
            .map { item ->
                item.copy(name = ctx.getString(item.type.stringId()))
            }
            .toTypedArray())


    }

    private suspend fun checkItemsForRemove() {
        // Удаление не нужных
        val notNeeded = MainMenuType.values().filter { it.added.not() }
        if (notNeeded.isNotEmpty())
            mainMenuDao.items().filter { type ->
                notNeeded.any { it == type.type }
            }.forEach {
                mainMenuDao.delete(it)
            }
    }

    companion object {
        const val tag = "updateMainMenu"

        fun addTask(ctx: Context): Operation {
            val task = OneTimeWorkRequestBuilder<UpdateMainMenuWorker>()
                .addTag(tag)
                .build()
            return WorkManager.getInstance(ctx).enqueueUniqueWork(
                tag + "Unique",
                ExistingWorkPolicy.KEEP,
                task
            )
        }
    }
}
