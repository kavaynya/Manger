package com.san.kir.background.logic.di

import androidx.work.WorkManager
import com.san.kir.background.logic.DownloadChaptersManager
import com.san.kir.background.logic.UpdateCatalogManager
import com.san.kir.background.logic.UpdateMangaManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.catalogWorkerRepository
import com.san.kir.data.chapterRepository
import com.san.kir.data.chapterWorkerRepository
import com.san.kir.data.mangaWorkerRepository

fun ManualDI.updateMangaManager() = UpdateMangaManager(workManager(), mangaWorkerRepository())
fun ManualDI.updateCatalogManager() = UpdateCatalogManager(workManager(), catalogWorkerRepository())

fun ManualDI.downloadChaptersManager() =
    DownloadChaptersManager(workManager(), chapterWorkerRepository(), chapterRepository())

internal fun ManualDI.workManager() = WorkManager.getInstance(application)
