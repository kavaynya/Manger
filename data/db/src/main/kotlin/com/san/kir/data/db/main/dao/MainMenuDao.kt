package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbMainMenuItem
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MainMenuDao : BaseDao<DbMainMenuItem> {
    @Query("SELECT * FROM mainmenuitems ORDER BY `order`")
    suspend fun items(): List<DbMainMenuItem>

    @Query("SELECT * FROM mainmenuitems ORDER BY `order`")
    fun loadItems(): Flow<List<DbMainMenuItem>>
}


