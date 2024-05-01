package com.san.kir.data.db.workers.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.workers.entities.DbMangaTask
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MangaTaskDao : BaseTaskDao<DbMangaTask> {

    @Query("SELECT * FROM manga_task")
    override fun loadItems(): Flow<List<DbMangaTask>>

    @Query("SELECT * FROM manga_task WHERE manga_id=:id")
    fun loadItemByMangaId(id: Long): Flow<DbMangaTask?>

    @Query("SELECT * FROM manga_task WHERE manga_id=:id")
    suspend fun itemByMangaId(id: Long): DbMangaTask?

    @Query("DELETE FROM manga_task WHERE id=:id")
    override suspend fun removeById(id: Long)

    @Query("DELETE FROM manga_task WHERE id IN (:ids)")
    suspend fun removeByIds(ids: List<Long>)

    @Query("DELETE FROM manga_task")
    override suspend fun clear()
}
