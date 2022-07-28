package com.san.kir.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.san.kir.data.models.base.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM ${Settings.tableName}")
    fun loadItems(): Flow<Settings>

    @Insert(onConflict = REPLACE)
    suspend fun update(item: Settings)
}
