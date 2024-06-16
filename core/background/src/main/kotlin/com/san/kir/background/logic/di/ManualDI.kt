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

public fun ManualDI.updateMangaManager(): UpdateMangaManager =
    UpdateMangaManager(workManager(), mangaWorkerRepository())

public fun ManualDI.updateCatalogManager(): UpdateCatalogManager =
    UpdateCatalogManager(workManager(), catalogWorkerRepository())

public fun ManualDI.downloadChaptersManager(): DownloadChaptersManager =
    DownloadChaptersManager(workManager(), chapterWorkerRepository(), chapterRepository())

internal fun ManualDI.workManager() = WorkManager.getInstance(application)
