package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbStatistic
import com.san.kir.data.db.main.views.ViewStatistic
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StatisticDao : BaseDao<DbStatistic> {
    @Query("SELECT * FROM simple_statistic")
    fun loadSimpleItems(): Flow<List<ViewStatistic>>

    @Query("SELECT SUM(all_time) FROM statistic")
    fun loadAllTime(): Flow<Long>

    @Query("SELECT * FROM statistic ORDER BY all_time DESC")
    fun loadItems(): Flow<List<DbStatistic>>

    @Query("SELECT * FROM statistic WHERE id IS :itemId")
    fun loadItemById(itemId: Long): Flow<DbStatistic?>

    @Query("SELECT * FROM statistic WHERE manga_id IS :mangaId")
    fun loadItemByMangaId(mangaId: Long): Flow<DbStatistic?>

    @Query("SELECT * FROM statistic")
    suspend fun items(): List<DbStatistic>

    @Query("SELECT * FROM statistic WHERE id IS :itemId")
    suspend fun itemById(itemId: Long): DbStatistic?

    @Query("SELECT * FROM statistic WHERE manga_id IS :mangaId")
    suspend fun itemByMangaId(mangaId: Long): DbStatistic?

    @Query("SELECT id FROM statistic WHERE manga_id IS :mangaId")
    suspend fun idByMangaId(mangaId: Long): Long?

    @Query("DELETE FROM statistic WHERE id=:itemId")
    suspend fun delete(itemId: Long)
}
