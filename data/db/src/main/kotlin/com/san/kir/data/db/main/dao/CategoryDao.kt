package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.custom.DbNameAndId
import com.san.kir.data.db.main.entites.DbCategory
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao : BaseDao<DbCategory> {

    @Query("SELECT COUNT(id) FROM categories")
    fun loadItemsCount(): Flow<Int>

    // Получение flow со списком всех элементов отсортированных по полю order
    @Query("SELECT * FROM categories ORDER BY ordering")
    fun loadItems(): Flow<List<DbCategory>>

    @Query("SELECT id, name FROM categories")
    fun loadNamesAndIds(): Flow<List<DbNameAndId>>

    // Получение flow со списком имен всех элементов отсортированных по полю order
    @Query("SELECT name FROM categories ORDER BY ordering")
    fun loadNames(): Flow<List<String>>

    // Получение flow с элементом по его названию
    @Query("SELECT * FROM categories WHERE name IS :name")
    fun loadItemByName(name: String): Flow<DbCategory?>

    // Получение flow с элементом по его id
    @Query("SELECT * FROM categories WHERE id IS :id")
    fun loadItemById(id: Long): Flow<DbCategory>

    // Получение всех элементов отсортированных по полю Порядок
    @Query("SELECT * FROM categories ORDER BY ordering")
    suspend fun items(): List<DbCategory>

    // Получение элемента по его id
    @Query("SELECT * FROM categories WHERE id IS :id")
    suspend fun itemById(id: Long): DbCategory

    // Получение элемента по его названию
    @Query("SELECT * FROM categories WHERE name IS :name")
    suspend fun itemByName(name: String): DbCategory

    @Query("SELECT name FROM categories ORDER BY ordering")
    suspend fun names(): List<String>
}


