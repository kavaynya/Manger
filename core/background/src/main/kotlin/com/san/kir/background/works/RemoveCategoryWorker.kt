package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.data.categoryDao
import com.san.kir.data.db.main.dao.CategoryDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.mangaDao
import com.san.kir.data.db.main.entites.DbCategory

/*
    Worker для удаления категории,
    У всей манги, которая была свазанна с удаляемой категорией,
    применяется категория по умолчанию
*/
class RemoveCategoryWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    private val categoryDao: CategoryDao = ManualDI.categoryDao
    private val mangaDao: MangaDao = ManualDI.mangaDao

    override suspend fun doWork(): Result {
        val categoryId = inputData.getLong(CAT, -1L)

        if (categoryId != -1L) {
            // Получение удаляемой категории
            val category = withDefaultContext {
                categoryDao.itemById(categoryId)
            }

            // Получение категории "Все"
            val categoryAll = withDefaultContext {
                categoryDao.defaultCategory(applicationContext)
            }

            kotlin.runCatching {
                withDefaultContext {
                    mangaDao.update(
                        mangaDao
                            // Получение всей манги, которая связана с удаляемой категорией
                            .itemsByCategoryId(category.id)
                            .map {
                                // Замена на категорию "Все"
                                it.copy(categoryId = categoryAll.id)
                            }
                    )
                    categoryDao.delete(category)
                }
            }.fold(
                onSuccess = {
                    return Result.success()
                },
                onFailure = {
                    it.printStackTrace()
                    return Result.failure()
                }
            )
        } else {
            return Result.retry()
        }
    }

    companion object {
        const val TAG = "removeCategory"
        const val CAT = "category_id"

        fun addTask(ctx: Context, category: DbCategory) {
            val data = workDataOf(CAT to category.id)
            val task = OneTimeWorkRequestBuilder<RemoveCategoryWorker>()
                .addTag(TAG)
                .setInputData(data)
                .build()
            WorkManager.getInstance(ctx).enqueue(task)
        }
    }
}
