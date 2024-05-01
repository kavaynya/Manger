package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.san.kir.data.db.main.entites.DbSettings
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun loadItem(): Flow<DbSettings>

    @Upsert
    suspend fun update(item: DbSettings)
}
