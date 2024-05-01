package com.san.kir.background.works

import android.content.Context
import androidx.work.WorkerParameters
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.chapterDao
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.dao.StorageDao
import com.san.kir.data.mangaDao
import com.san.kir.data.db.main.entites.DbManga
import com.san.kir.data.db.main.entites.getSizeAndIsNew
import com.san.kir.data.storageDao

class AllChapterDelete(
    appContext: Context,
    workerParams: WorkerParameters,
) : ChapterDeleteWorker(appContext, workerParams) {

    private val mangaDao: MangaDao = ManualDI.mangaDao
    private val chapterDao: ChapterDao = ManualDI.chapterDao
    private val storageDao: StorageDao = ManualDI.storageDao

    override suspend fun doWork(): Result {
        val mangaId = inputData.getLong("id", -1)

        return kotlin.runCatching {
            val manga = mangaDao.itemById(mangaId)
            deleteAllChapters(manga)
            updateStorageItem(manga)
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

    private fun deleteAllChapters(manga: DbManga) {
        getFullPath(manga.path).deleteRecursively()
    }

    private suspend fun updateStorageItem(manga: DbManga) {
        val storageItem = storageDao.items().first { it.path == getFullPath(manga.path).shortPath }

        val file = getFullPath(storageItem.path)

        storageDao.update(
            storageItem.getSizeAndIsNew(file, false, chapterDao.itemsByMangaId(manga.id))
        )
    }
}
