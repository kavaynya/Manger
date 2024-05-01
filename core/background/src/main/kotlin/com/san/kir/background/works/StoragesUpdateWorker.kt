package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.chapterDao
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.dao.StorageDao
import com.san.kir.data.db.main.dao.itemByPath
import com.san.kir.data.mangaDao
import com.san.kir.data.db.main.entites.DbStorage
import com.san.kir.data.db.main.entites.getSizeAndIsNew
import com.san.kir.data.storageDao

/*
    Worker для обновления данных о занимаемом месте
*/
class StoragesUpdateWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    private val storageDao: StorageDao = ManualDI.storageDao
    private val chapterDao: ChapterDao = ManualDI.chapterDao
    private val mangaDao: MangaDao = ManualDI.mangaDao

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            val list = storageDao.items()

            val storageList = getFullPath(DIR.MANGA).listFiles()

            if (storageList != null && (list.isEmpty() || storageList.size != list.size)) {
                storageList.forEach { dir ->
                    dir.listFiles()?.forEach { item ->
                        if (list.none { it.name == item.name }) {
                            storageDao.insert(
                                DbStorage(
                                    name = item.name,
                                    path = item.shortPath,
                                    catalogName = dir.name
                                )
                            )
                        }
                    }
                }
            }

            list.map { storage ->
                val file = getFullPath(storage.path)
                val manga = mangaDao.itemByPath(file)

                storageDao.update(
                    storage.getSizeAndIsNew(
                        file,
                        manga == null,
                        manga?.let { chapterDao.itemsByMangaId(it.id) })
                )
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
    }

    companion object {
        const val tag = "updateStorages"

        fun runTask(ctx: Context) {
            val task = OneTimeWorkRequestBuilder<StoragesUpdateWorker>()
                .addTag(tag)
                .build()
            WorkManager.getInstance(ctx).enqueue(task)
        }
    }
}
