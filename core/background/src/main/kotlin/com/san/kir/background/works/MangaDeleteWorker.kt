package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.chapterRepository
import com.san.kir.data.mangaRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

public class MangaDeleteWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val mangaRepository = ManualDI.mangaRepository()
    private val chaptersRepository = ManualDI.chapterRepository()

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
                Timber.tag("MangaDeleteWorker").e(it)
                Result.failure()
            }
        )
    }

    private suspend fun removeWithChapters(mangaId: Long, withFiles: Boolean = false) {
        val manga = mangaRepository.item(mangaId) ?: return

        mangaRepository.delete(manga)

        val ids = chaptersRepository.allItems(manga.id).map { it.id }
        chaptersRepository.delete(ids)

        if (withFiles) {
            getFullPath(manga.path).deleteRecursively()
        }
    }

    public companion object {
        private const val TAG = "mangaDelete"
        private const val WITH_FILES = "withFiles"

        public fun addTask(mangaId: Long, withFiles: Boolean = false) {
            val data = workDataOf("id" to mangaId, WITH_FILES to withFiles)
            val deleteManga = OneTimeWorkRequestBuilder<MangaDeleteWorker>()
                .setInputData(data)
                .addTag(TAG)
                .build()
            ManualDI.workManager().enqueue(deleteManga)
        }

        public fun workInfos(): Flow<List<WorkInfo>> =
            ManualDI.workManager().getWorkInfosByTagFlow(TAG)
    }
}
