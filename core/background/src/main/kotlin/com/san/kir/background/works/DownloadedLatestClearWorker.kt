package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterDao
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.entites.action
import com.san.kir.data.models.utils.ChapterStatus

class DownloadedLatestClearWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val chapterDao: ChapterDao = ManualDI.chapterDao

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            chapterDao.update(
                *chapterDao.items()
                    .filter { it.isInUpdate }
                    .filter { it.action == ChapterStatus.DELETE }
                    .map { it.copy(isInUpdate = false) }
                    .toTypedArray()
            )
        }.fold(
            onSuccess = {
                return Result.success()
            },
            onFailure = {
                it.printStackTrace()
                return Result.failure()
            }
        )
    }
}
