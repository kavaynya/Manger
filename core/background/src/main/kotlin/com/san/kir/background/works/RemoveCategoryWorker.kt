package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.data.categoryRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Category

/*
    Worker для удаления категории,
    У всей манги, которая была свазанна с удаляемой категорией,
    применяется категория по умолчанию
*/
public class RemoveCategoryWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    private val categoryRepository = ManualDI.categoryRepository()
    private val mangaRepository = ManualDI.mangaRepository()

    override suspend fun doWork(): Result {
        val categoryId = inputData.getLong(CAT, -1L)

        if (categoryId != -1L) {
            // Получение удаляемой категории
            val category = withDefaultContext {
                categoryRepository.item(categoryId)
            }

            // Получение категории "Все"
            val categoryAll = withDefaultContext {
                categoryRepository.defaultCategory()
            }

            kotlin.runCatching {
                withDefaultContext {
                    mangaRepository.save(
                        mangaRepository
                            // Получение всей манги, которая связана с удаляемой категорией
                            .itemsByCategoryId(category.id)
                            .map {
                                // Замена на категорию "Все"
                                it.copy(categoryId = categoryAll.id)
                            }
                    )
                    categoryRepository.delete(category)
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

    public companion object {
        private const val TAG = "removeCategory"
        private const val CAT = "category_id"

        public fun addTask(category: Category) {
            val data = workDataOf(CAT to category.id)
            val task = OneTimeWorkRequestBuilder<RemoveCategoryWorker>()
                .addTag(TAG)
                .setInputData(data)
                .build()
            ManualDI.workManager().enqueue(task)
        }
    }
}
