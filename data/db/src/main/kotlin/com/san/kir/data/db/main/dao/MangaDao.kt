package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.custom.DbMinimalStorageManga
import com.san.kir.data.db.main.custom.DbMinimalTaskManga
import com.san.kir.data.db.main.custom.DbNameAndId
import com.san.kir.data.db.main.entites.DbManga
import com.san.kir.data.db.main.views.ViewManga
import com.san.kir.data.db.main.views.ViewMangaWithChapterCounts
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MangaDao : BaseDao<DbManga> {

    @Query("SELECT COUNT(id) FROM manga")
    fun loadItemsCount(): Flow<Int>

    // Получение flow со списком всех элементов
    @Query("SELECT * FROM manga")
    fun loadItems(): Flow<List<DbManga>>

    // Получение flow со списком всех элементов из view
    @Query(
        "SELECT id, " +
                "name, " +
                "isUpdate, " +
                "(SELECT name FROM categories WHERE manga.category_id = categories.id) AS category " +
                "FROM manga"
    )
    fun loadMiniItems(): Flow<List<DbMinimalTaskManga>>

    // Получение всех упрощенных элементов из View
    @Query("SELECT * FROM simple_manga")
    fun loadSimpleItems(): Flow<List<ViewManga>>

    @Query("SELECT id, name FROM manga WHERE isUpdate=1")
    fun loadNamesAndIds(): Flow<List<DbNameAndId>>

    @Query("SELECT * FROM libarary_manga ORDER BY name")
    fun loadItemsWithChapterCounts(): Flow<List<ViewMangaWithChapterCounts>>

    // Получение flow с элементом по его названию
    @Query("SELECT * FROM manga WHERE name IS :name")
    fun loadItemByName(name: String): Flow<DbManga?>

    // Получение flow с элементом по его id
    @Query("SELECT * FROM manga WHERE id=:id")
    fun loadItemById(id: Long): Flow<DbManga?>

    @Query("SELECT * FROM libarary_manga WHERE id=:id")
    fun loadItemWithChapterCountsById(id: Long): Flow<ViewMangaWithChapterCounts?>

    // Получение всех элементов
    @Query("SELECT * FROM manga")
    suspend fun items(): List<DbManga>

    // Получение id всех элементов
    @Query("SELECT id FROM manga WHERE isUpdate = :isUpdate")
    suspend fun itemIds(isUpdate: Boolean = true): List<Long>

    @Query("SELECT id, name, logo, path FROM manga")
    suspend fun specItems(): List<DbMinimalStorageManga>

    // Получение элемента по названию
    @Query("SELECT id FROM manga WHERE name IN (:names)")
    suspend fun itemIdsByNames(names: List<String>): List<Long>

    // Получение элемента по id
    @Query("SELECT * FROM manga WHERE id IS :id")
    suspend fun itemById(id: Long): DbManga?

    // Получение элементов по id категории
    @Query("SELECT * FROM manga WHERE category_id IS :id")
    suspend fun itemsByCategoryId(id: Long): List<DbManga>

    // Получение id элементов по id категории
    @Query("SELECT id FROM manga WHERE category_id=:id AND isUpdate=:isUpdate")
    suspend fun itemIdsByCategoryId(id: Long, isUpdate: Boolean = true): List<Long>

    // Обновление поля isUpdate
    @Query("UPDATE manga SET isUpdate = :isUpdate WHERE id = :id")
    suspend fun updateIsUpdate(id: Long, isUpdate: Boolean)

    // Обновление поля categoryId
    @Query("UPDATE manga SET category_id = :categoryId WHERE id = :id")
    suspend fun updateCategory(id: Long, categoryId: Long)

    @Query("UPDATE manga SET color=:color WHERE id=:id")
    suspend fun updateColor(id: Long, color: Int)
}
