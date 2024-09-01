package com.san.kir.data

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.db.catalog.CatalogDb
import com.san.kir.data.db.catalog.repo.CatalogsRepository
import com.san.kir.data.db.main.RoomDB
import com.san.kir.data.db.main.repo.AccountRepository
import com.san.kir.data.db.main.repo.AccountsMangaRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.db.main.repo.ChapterRepository
import com.san.kir.data.db.main.repo.MainMenuRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.PlannedRepository
import com.san.kir.data.db.main.repo.SettingsRepository
import com.san.kir.data.db.main.repo.StatisticsRepository
import com.san.kir.data.db.main.repo.StorageRepository
import com.san.kir.data.db.workers.WorkersDb
import com.san.kir.data.db.workers.repo.CatalogWorkerRepository
import com.san.kir.data.db.workers.repo.ChapterWorkerRepository
import com.san.kir.data.db.workers.repo.MangaWorkerRepository

/** Main Database */
private val ManualDI.appDatabase: RoomDB
    get() = RoomDB.getDatabase(application)

public fun ManualDI.accountMangaRepository(): AccountsMangaRepository =
    AccountsMangaRepository(appDatabase.accountMangaDao())

public fun ManualDI.accountsRepository(): AccountRepository =
    AccountRepository(appDatabase.accountDao())

public fun ManualDI.categoryRepository(): CategoryRepository =
    CategoryRepository(lazy { application }, appDatabase.categoryDao())

public fun ManualDI.chapterRepository(): ChapterRepository =
    ChapterRepository(appDatabase.chapterDao())

public fun ManualDI.mainMenuRepository(): MainMenuRepository =
    MainMenuRepository(appDatabase.mainMenuDao())

public fun ManualDI.mangaRepository(): MangaRepository = MangaRepository(appDatabase.mangaDao())
public fun ManualDI.plannedRepository(): PlannedRepository =
    PlannedRepository(appDatabase.plannedDao())
public fun ManualDI.lazyPlannedRepository(): Lazy<PlannedRepository> = lazy { plannedRepository() }

public fun ManualDI.settingsRepository(): SettingsRepository =
    SettingsRepository(appDatabase.settingsDao())

public fun ManualDI.statisticsRepository(): StatisticsRepository =
    StatisticsRepository(appDatabase.statisticDao())

public fun ManualDI.storageRepository(): StorageRepository =
    StorageRepository(appDatabase.storageDao())


/** Database for catalogs*/
private fun ManualDI.catalogDatabase(catalogName: String) =
    CatalogDb.getDatabase(application, catalogName)

public fun ManualDI.catalogsRepository(): CatalogsRepository = CatalogsRepository(::catalogDatabase)

/** Database for task managers */
private fun ManualDI.workerDatabase(): WorkersDb = WorkersDb.getDatabase(application)

public fun ManualDI.mangaWorkerRepository(): MangaWorkerRepository =
    MangaWorkerRepository(workerDatabase().mangasDao)

public fun ManualDI.chapterWorkerRepository(): ChapterWorkerRepository =
    ChapterWorkerRepository(workerDatabase().chaptersDao)

public fun ManualDI.catalogWorkerRepository(): CatalogWorkerRepository =
    CatalogWorkerRepository(workerDatabase().catalogDao)
