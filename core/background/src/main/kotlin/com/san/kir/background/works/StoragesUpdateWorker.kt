package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.chapterRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Storage
import com.san.kir.data.models.main.getSizes
import com.san.kir.data.storageRepository

/*
    Worker для обновления данных о занимаемом месте
*/
class StoragesUpdateWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    private val storageRepository = ManualDI.storageRepository()
    private val chaptersRepository = ManualDI.chapterRepository()
    private val mangaRepository = ManualDI.mangaRepository()

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            val list = storageRepository.items()

            val storageList = getFullPath(DIR.MANGA).listFiles()

            if (storageList != null && (list.isEmpty() || storageList.size != list.size)) {
                storageList.forEach { dir ->
                    dir.listFiles()?.forEach { item ->
                        if (list.none { it.name == item.name }) {
                            storageRepository.save(
                                Storage(
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
                val manga = mangaRepository.itemByPath(file)

                storageRepository.save(
                    storage.getSizes(file, manga?.let { chaptersRepository.allItems(it.id) })
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

        fun runTask() {
            val task = OneTimeWorkRequestBuilder<StoragesUpdateWorker>()
                .addTag(tag)
                .build()
            ManualDI.workManager().enqueue(task)
        }
    }
}
