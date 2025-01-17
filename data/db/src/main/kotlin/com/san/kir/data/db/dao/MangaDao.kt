package com.san.kir.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.models.base.Manga
import com.san.kir.data.models.extend.MangaLogo
import com.san.kir.data.models.extend.MiniManga
import com.san.kir.data.models.extend.NameAndId
import com.san.kir.data.models.extend.SimplifiedManga
import kotlinx.coroutines.flow.Flow
import java.io.File

@Dao
interface MangaDao : BaseDao<Manga> {

    @Query("SELECT COUNT(id) FROM manga")
    fun loadItemsCount(): Flow<Int>

    // Получение flow со списком всех элементов
    @Query("SELECT * FROM manga")
    fun loadItems(): Flow<List<Manga>>

    // Получение flow со списком всех элементов из view
    @Query(
        "SELECT id, name, isUpdate, " +
                "(SELECT name FROM categories WHERE manga.category_id = categories.id) AS category " +
                " FROM manga"
    )
    fun loadMiniItems(): Flow<List<MiniManga>>

    // Получение всех упрощенных элементов из View
    @Query("SELECT * FROM simple_manga")
    fun loadSimpleItems(): Flow<List<SimplifiedManga>>

    @Query("SELECT id, name FROM manga WHERE isUpdate=1")
    fun loadNamesAndIds(): Flow<List<NameAndId>>

    // Получение flow с элементом по его названию
    @Query("SELECT * FROM `manga` WHERE `name` IS :name")
    fun loadItemByName(name: String): Flow<Manga?>

    // Получение flow с элементом по его id
    @Query("SELECT * FROM manga WHERE id IS :id")
    fun loadItemById(id: Long): Flow<Manga?>

    // Получение всех элементов
    @Query("SELECT * FROM manga")
    suspend fun items(): List<Manga>

    // Получение id всех элементов
    @Query("SELECT id FROM manga WHERE isUpdate = :isUpdate")
    suspend fun itemIds(isUpdate: Boolean = true): List<Long>

    @Query("SELECT id, logo, path FROM manga")
    suspend fun specItems(): List<MangaLogo>

    @Query("SELECT shortLink FROM manga")
    suspend fun links(): List<String>

    // Получение элемента по названию
    @Query("SELECT id FROM manga WHERE name IN (:names)")
    suspend fun itemIdsByNames(names: List<String>): List<Long>

    // Получение элемента по id
    @Query("SELECT * FROM manga WHERE id IS :id")
    suspend fun itemById(id: Long): Manga

    // Получение элементов по id категории
    @Query("SELECT * FROM manga WHERE category_id IS :id")
    suspend fun itemsByCategoryId(id: Long): List<Manga>

    // Получение id элементов по id категории
    @Query("SELECT id FROM manga WHERE category_id IS :id AND isUpdate = :isUpdate")
    suspend fun itemIdsByCategoryId(id: Long, isUpdate: Boolean = true): List<Long>

    // Обновление поля isUpdate
    @Query("UPDATE manga SET isUpdate = :isUpdate WHERE id = :id")
    fun update(id: Long, isUpdate: Boolean)

    // Обновление поля categoryId
    @Query("UPDATE manga SET category_id = :categoryId WHERE id = :id")
    fun update(id: Long, categoryId: Long)
}

// Получение элемента по его пути хранения
suspend fun MangaDao.itemByPath(file: File): MangaLogo? {
    return specItems().firstOrNull { getFullPath(it.path) == file }
}
