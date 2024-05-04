package com.san.kir.background.works

import android.content.Context
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.delChapters
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.chapterRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Manga
import com.san.kir.data.models.main.getSizes
import com.san.kir.data.storageRepository
import timber.log.Timber

class ReadChapterDelete(
    appContext: Context,
    workerParams: WorkerParameters,
) : ChapterDeleteWorker(appContext, workerParams) {

    private val mangaRepository = ManualDI.mangaRepository()
    private val chaptersRepository = ManualDI.chapterRepository()
    private val storageRepository = ManualDI.storageRepository()

    override suspend fun doWork(): Result {
        val mangaId = inputData.getLong("id", -1)

        return kotlin.runCatching {
            val manga = mangaRepository.item(mangaId)
            if (manga != null) {
                deleteReadChapters(manga)
                updateStorageItem(manga)
            } else {
                Timber.tag("ReadChapterDelete").e("Не найдена манга с id -> $mangaId")
            }
        }.fold(
            onSuccess = { Result.success() },
            onFailure = {
                Timber.tag("ReadChapterDelete").e(it)
                Result.failure()
            }
        )
    }

    private suspend fun deleteReadChapters(manga: Manga) {
        val chapters = chaptersRepository
            .allItems(manga.id)
            .filter { chapter -> chapter.isRead }
            .map { it.path }

        delChapters(chapters)
    }

    private suspend fun updateStorageItem(manga: Manga) {
        val storageItem = storageRepository.items()
            .first { it.path == getFullPath(manga.path).shortPath }

        val file = getFullPath(storageItem.path)

        storageRepository.save(
            storageItem.getSizes(file, chaptersRepository.allItems(manga.id))
        )
    }
}
