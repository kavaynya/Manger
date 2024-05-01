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

fun ManualDI.accountMangaRepository() = AccountsMangaRepository(appDatabase.accountMangaDao())
fun ManualDI.accountsRepository() = AccountRepository(appDatabase.accountDao())
fun ManualDI.categoryRepository() = CategoryRepository(appDatabase.categoryDao())
fun ManualDI.chapterRepository() = ChapterRepository(appDatabase.chapterDao())
fun ManualDI.mainMenuRepository() = MainMenuRepository(appDatabase.mainMenuDao())
fun ManualDI.mangaRepository() = MangaRepository(appDatabase.mangaDao())
fun ManualDI.plannedRepository() = PlannedRepository(appDatabase.plannedDao())
fun ManualDI.settingsRepository() = SettingsRepository(appDatabase.settingsDao())
fun ManualDI.statisticsRepository() = StatisticsRepository(appDatabase.statisticDao())
fun ManualDI.storageRepository() = StorageRepository(appDatabase.storageDao())


/** Database for catalogs*/
private fun ManualDI.catalogDatabase(catalogName: String) =
    CatalogDb.getDatabase(application, catalogName)

fun ManualDI.catalogsRepository() = CatalogsRepository(::catalogDatabase)

/** Database for task managers */
private fun ManualDI.workerDatabase(): WorkersDb = WorkersDb.getDatabase(application)

val ManualDI.mangaWorkerRepository: MangaWorkerRepository
    get() = MangaWorkerRepository(workerDatabase().mangasDao)

val ManualDI.chapterWorkerRepository: ChapterWorkerRepository
    get() = ChapterWorkerRepository(workerDatabase().chaptersDao)

val ManualDI.catalogWorkerRepository: CatalogWorkerRepository
    get() = CatalogWorkerRepository(workerDatabase().catalogDao)
