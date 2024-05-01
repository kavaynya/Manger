package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbPlannedTask
import com.san.kir.data.db.main.custom.DbSimplifiedPlannedTask
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlannedDao : BaseDao<DbPlannedTask> {

    // Получение flow с количеством элементов в таблице
    @Query("SELECT COUNT(*) FROM planned_task")
    fun loadItemsCount(): Flow<Int>

    // Получение flow со списком всех элементов из view
    @Query(
        "SELECT id, " +
                "IFNULL((SELECT name FROM manga WHERE planned_task.manga_id=manga.id),'') AS manga, " +
                "group_name, " +
                "IFNULL((SELECT name FROM categories WHERE planned_task.category_id=categories.id), '') AS category, " +
                "catalog, " +
                "type, " +
                "is_enabled, " +
                "period, " +
                "day_of_week, " +
                "hour, " +
                "minute " +
                "FROM planned_task ORDER BY id"
    )
    fun loadSimpleItems(): Flow<List<DbSimplifiedPlannedTask>>

    // Получение flow с элементов по его id
    @Query("SELECT * FROM planned_task WHERE id IS :taskId")
    fun loadItemById(taskId: Long): Flow<DbPlannedTask?>

    // Получение элемента по его id
    @Query("SELECT * FROM planned_task WHERE id IS :taskId")
    suspend fun itemById(taskId: Long): DbPlannedTask?

    // Обновление поля isEnable
    @Query("UPDATE planned_task SET is_enabled = :enable WHERE id = :id")
    suspend fun update(id: Long, enable: Boolean)
}

