package com.san.kir.data.db.workers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.san.kir.data.db.workers.dao.CatalogTaskDao
import com.san.kir.data.db.workers.dao.ChapterTaskDao
import com.san.kir.data.db.workers.dao.MangaTaskDao
import com.san.kir.data.db.main.typeConverters.ListStringConverter
import com.san.kir.data.db.workers.entities.DbCatalogTask
import com.san.kir.data.db.workers.entities.DbChapterTask
import com.san.kir.data.db.workers.entities.DbMangaTask

@Database(
    entities = [DbCatalogTask::class, DbMangaTask::class, DbChapterTask::class],
    version = 1,
    autoMigrations = []
)
@TypeConverters(ListStringConverter::class)
internal abstract class WorkersDb : RoomDatabase() {
    companion object {
        private lateinit var sDb: WorkersDb

        fun getDatabase(context: Context): WorkersDb {
            if (Companion::sDb.isInitialized.not())
                synchronized(WorkersDb::class.java) {
                    if (Companion::sDb.isInitialized.not())
                        sDb = Room
                            .databaseBuilder(
                                /* context = */ context.applicationContext,
                                /* klass = */ WorkersDb::class.java,
                                /* name = */ WorkersDb::class.java.name + ".db"
                            )
                            .addMigrations(*Migrate.migrations)
                            .build()
                }

            return sDb
        }
    }

    abstract val catalogDao: CatalogTaskDao
    abstract val mangasDao: MangaTaskDao
    abstract val chaptersDao: ChapterTaskDao

    object Migrate {
        val migrations: Array<Migration> = arrayOf()
    }
}
