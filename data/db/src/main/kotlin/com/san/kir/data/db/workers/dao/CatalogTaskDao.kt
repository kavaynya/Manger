package com.san.kir.data.db.workers.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.workers.entities.DbCatalogTask
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CatalogTaskDao : BaseTaskDao<DbCatalogTask> {

    @Query("SELECT * FROM catalog_task")
    override fun loadItems(): Flow<List<DbCatalogTask>>

    @Query("SELECT * FROM catalog_task WHERE name=:name")
    fun loadItemByName(name: String): Flow<DbCatalogTask?>

    @Query("SELECT * FROM catalog_task WHERE name=:name")
    suspend fun itemByName(name: String): DbCatalogTask?

    @Query("DELETE FROM catalog_task WHERE id=:id")
    override suspend fun removeById(id: Long)

    @Query("DELETE FROM catalog_task")
    override suspend fun clear()
}
