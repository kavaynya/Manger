package com.san.kir.data

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.db.CatalogDb
import com.san.kir.data.db.RoomDB
import com.san.kir.data.db.WorkersDb
import com.san.kir.data.db.dao.CatalogTaskDao
import com.san.kir.data.db.dao.CategoryDao
import com.san.kir.data.db.dao.ChapterDao
import com.san.kir.data.db.dao.ChapterTaskDao
import com.san.kir.data.db.dao.MainMenuDao
import com.san.kir.data.db.dao.MangaDao
import com.san.kir.data.db.dao.MangaTaskDao
import com.san.kir.data.db.dao.PlannedDao
import com.san.kir.data.db.dao.SettingsDao
import com.san.kir.data.db.dao.ShikimoriDao
import com.san.kir.data.db.dao.StorageDao

/** Main Database */
val ManualDI.appDatabase: RoomDB
    get() = RoomDB.getDatabase(context)

val ManualDI.plannedDao: PlannedDao
    get() = appDatabase.plannedDao()

val ManualDI.lazyPlannedDao: Lazy<PlannedDao>
    get() = lazy { plannedDao }

val ManualDI.settingsDao: SettingsDao
    get() = appDatabase.settingsDao()

val ManualDI.mangaDao: MangaDao
    get() = appDatabase.mangaDao()

val ManualDI.categoryDao: CategoryDao
    get() = appDatabase.categoryDao()

val ManualDI.storageDao: StorageDao
    get() = appDatabase.storageDao()

val ManualDI.chapterDao: ChapterDao
    get() = appDatabase.chapterDao()

val ManualDI.mainMenuDao: MainMenuDao
    get() = appDatabase.mainMenuDao()

val ManualDI.shikimoriDao: ShikimoriDao
    get() = appDatabase.shikimoriDao()

val ManualDI.statisticDao
    get() = appDatabase.statisticDao()

/** Database for catalogs*/
fun ManualDI.catalogDatabase(catalogName: String): CatalogDb {
    return CatalogDb.getDatabase(context, catalogName)
}

val ManualDI.catalogDatabaseFactory: (String) -> CatalogDb
    get() = ::catalogDatabase

/** Database for task managers */
val ManualDI.workerDatabase: WorkersDb
    get() = WorkersDb.getDatabase(context)

val ManualDI.mangaTaskDao: MangaTaskDao
    get() = workerDatabase.mangasDao

val ManualDI.chapterTaskDao: ChapterTaskDao
    get() = workerDatabase.chaptersDao

val ManualDI.catalogTaskDao: CatalogTaskDao
    get() = workerDatabase.catalogDao
