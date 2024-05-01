package com.san.kir.data.db.workers.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.workers.entities.DbChapterTask
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ChapterTaskDao : BaseTaskDao<DbChapterTask> {

    @Query("SELECT * FROM chapter_task")
    override fun loadItems(): Flow<List<DbChapterTask>>

    @Query("SELECT * FROM chapter_task WHERE chapter_id=:id")
    fun loadItemByChapterId(id: Long): Flow<DbChapterTask?>

    @Query("SELECT * FROM chapter_task WHERE chapter_id=:id")
    suspend fun itemByChapterId(id: Long): DbChapterTask?

    @Query("DELETE FROM chapter_task WHERE id=:id")
    override suspend fun removeById(id: Long)

    @Query("DELETE FROM chapter_task WHERE id IN (:ids)")
    suspend fun removeByIds(ids: List<Long>)

    @Query("DELETE FROM chapter_task")
    override suspend fun clear()
}
