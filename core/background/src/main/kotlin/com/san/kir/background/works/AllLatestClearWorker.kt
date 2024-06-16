package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterRepository
import timber.log.Timber

internal class AllLatestClearWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val chaptersRepository = ManualDI.chapterRepository()

    override suspend fun doWork(): Result {
        runCatching {
            chaptersRepository.save(
                chaptersRepository.allItems()
                    .filter { it.isInUpdate }
                    .map { it.copy(isInUpdate = false) }
            )
        }.fold(
            onSuccess = {
                return Result.success()
            },
            onFailure = {
                Timber.tag("AllLatestClearWorker").e(it)
                return Result.failure()
            }
        )
    }
}

