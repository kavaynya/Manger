package com.san.kir.background.logic.di

import com.san.kir.background.logic.DownloadChaptersManager
import com.san.kir.background.logic.UpdateCatalogManager
import com.san.kir.background.logic.UpdateMangaManager
import com.san.kir.background.logic.repo.CatalogRepository
import com.san.kir.background.logic.repo.CatalogWorkerRepository
import com.san.kir.background.logic.repo.ChapterRepository
import com.san.kir.background.logic.repo.ChapterWorkerRepository
import com.san.kir.background.logic.repo.MangaRepository
import com.san.kir.background.logic.repo.MangaWorkerRepository
import com.san.kir.background.logic.repo.SettingsRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.catalogDatabaseFactory
import com.san.kir.data.catalogTaskDao
import com.san.kir.data.chapterDao
import com.san.kir.data.chapterTaskDao
import com.san.kir.data.mangaDao
import com.san.kir.data.mangaTaskDao
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.settingsDao
import com.san.kir.data.statisticDao

private var singletonUpdateMangaManager: UpdateMangaManager? = null
private var singletonDownloadChaptersManager: DownloadChaptersManager? = null
private var singletonUpdateCatalogManager: UpdateCatalogManager? = null

val ManualDI.updateMangaManager: UpdateMangaManager
    get() = singletonUpdateMangaManager ?: run {
        val instance = UpdateMangaManager(context, mangaWorkerRepository)
        singletonUpdateMangaManager = instance
        instance
    }

val ManualDI.downloadChaptersManager: DownloadChaptersManager
    get() = singletonDownloadChaptersManager ?: run {
        val instance = DownloadChaptersManager(context, chapterWorkerRepository, chapterRepository)
        singletonDownloadChaptersManager = instance
        instance
    }

val ManualDI.updateCatalogManager: UpdateCatalogManager
    get() = singletonUpdateCatalogManager ?: run {
        val instance = UpdateCatalogManager(context, catalogWorkerRepository)
        singletonUpdateCatalogManager = instance
        instance
    }

internal val ManualDI.mangaWorkerRepository: MangaWorkerRepository
    get() = MangaWorkerRepository(mangaTaskDao)

internal val ManualDI.chapterWorkerRepository: ChapterWorkerRepository
    get() = ChapterWorkerRepository(chapterTaskDao)

internal val ManualDI.catalogWorkerRepository: CatalogWorkerRepository
    get() = CatalogWorkerRepository(catalogTaskDao)

internal val ManualDI.chapterRepository: ChapterRepository
    get() = ChapterRepository(siteCatalogsManager, chapterDao, statisticDao)

internal val ManualDI.mangaRepository: MangaRepository
    get() = MangaRepository(mangaDao, chapterDao)

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)

internal val ManualDI.catalogRepository: CatalogRepository
    get() = CatalogRepository(catalogDatabaseFactory, siteCatalogsManager)
