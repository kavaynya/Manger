package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterRepository
import com.san.kir.data.models.main.action
import com.san.kir.data.models.utils.ChapterStatus
import timber.log.Timber

class DownloadedLatestClearWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val chaptersRepository = ManualDI.chapterRepository()

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            chaptersRepository.save(
                chaptersRepository.allItems()
                    .filter { it.isInUpdate }
                    .filter { it.action == ChapterStatus.DELETE }
                    .map { it.copy(isInUpdate = false) }
            )
        }.fold(
            onSuccess = {
                return Result.success()
            },
            onFailure = {
                Timber.tag("DownloadedLatestClearWorker").e(it)
                return Result.failure()
            }
        )
    }
}
