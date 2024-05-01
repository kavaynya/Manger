package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.chapterDao
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.mangaDao

class MangaDeleteWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val mangaDao: MangaDao = ManualDI.mangaDao
    private val chapterDao: ChapterDao = ManualDI.chapterDao

    override suspend fun doWork(): Result {

        val mangaId = inputData.getLong("id", -1)
        val withFiles = inputData.getBoolean(WITH_FILES, false)

        return runCatching {
            removeWithChapters(mangaId, withFiles)
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = {
                it.printStackTrace()
                Result.failure()
            }
        )
    }

    private suspend fun removeWithChapters(mangaId: Long, withFiles: Boolean = false) {
        val manga = mangaDao.itemById(mangaId)

        mangaDao.delete(manga)

        with(chapterDao.itemsByMangaId(manga.id)) {
            chapterDao.delete(this)
        }

        if (withFiles) {
            getFullPath(manga.path).deleteRecursively()
        }
    }

    companion object {
        const val TAG = "mangaDelete"
        const val WITH_FILES = "withFiles"

        fun addTask(
            mangaId: Long,
            withFiles: Boolean = false,
            ctx: Context = ManualDI.context,
        ) {
            val data = workDataOf("id" to mangaId, WITH_FILES to withFiles)
            val deleteManga = OneTimeWorkRequestBuilder<MangaDeleteWorker>()
                .setInputData(data)
                .addTag(TAG)
                .build()
            WorkManager.getInstance(ctx).enqueue(deleteManga)
        }
    }
}
