package com.san.kir.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.san.kir.data.models.base.Category
import com.san.kir.data.models.extend.CategoryWithMangas
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<Category> {
    @Query("SELECT * FROM `${Category.tableName}` ORDER BY ${Category.Col.order}")
    suspend fun getItems(): List<Category>

    @Query("SELECT * FROM `${Category.tableName}` ORDER BY ${Category.Col.order}")
    fun loadItems(): Flow<List<Category>>

    @Query("SELECT * FROM `${Category.tableName}` WHERE `name` IS :name")
    fun loadItem(name: String): Flow<Category>

    @Transaction
    @Query(
        "SELECT " +
                "${Category.Col.name}, " +
                "${Category.Col.typeSort}, " +
                "${Category.Col.isReverseSort}, " +
                "${Category.Col.spanPortrait}, " +
                "${Category.Col.spanLandscape}, " +
                "${Category.Col.isLargePortrait}, " +
                "${Category.Col.isLargeLandscape} " +
                "FROM `${Category.tableName}` " +
                "WHERE ${Category.Col.isVisible} IS 1 " +
                "ORDER BY ${Category.Col.order}"
    )
    fun loadSpecItems(): Flow<List<CategoryWithMangas>>
}
