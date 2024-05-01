package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbStorage
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StorageDao : BaseDao<DbStorage> {

    @Query("SELECT SUM(sizeFull) FROM StorageItem")
    fun loadFullSize(): Flow<Double>

    @Query("SELECT * FROM StorageItem ORDER BY sizeFull DESC")
    fun loadItems(): Flow<List<DbStorage>>

    @Query("SELECT * FROM StorageItem WHERE path IS :shortPath")
    fun loadItemByPath(shortPath: String): Flow<DbStorage?>

    @Query("SELECT * FROM StorageItem WHERE path IS :shortPath")
    suspend fun itemByPath(shortPath: String): DbStorage?

    @Query("SELECT * FROM StorageItem ORDER BY sizeFull DESC")
    suspend fun items(): List<DbStorage>
}
