package com.san.kir.data.db.catalog.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.catalog.custom.DbSimplifiedCatalogItem
import com.san.kir.data.db.catalog.entities.DbSiteCatalogElement
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SiteCatalogDao : BaseDao<DbSiteCatalogElement> {

    @Query("SELECT * FROM items LIMIT :limit OFFSET :offset")
    suspend fun items(limit: Int = 500, offset: Int = 0): List<DbSiteCatalogElement>

    @Query("SELECT id, catalogName, name, statusEdition, shotLink, link, logo, genres, type, authors, dateId, populate FROM items")
    fun loadMinitItems(): Flow<List<DbSimplifiedCatalogItem>>

    @Query("SELECT id, catalogName, name, statusEdition, shotLink, link, logo, genres, type, authors, dateId, populate FROM items")
    suspend fun miniItems(): List<DbSimplifiedCatalogItem>

    @Query("SELECT * FROM items WHERE id=:id")
    suspend fun itemById(id: Long): DbSiteCatalogElement?

    @Query("SELECT * FROM items WHERE link=:link")
    suspend fun itemByLink(link: String): DbSiteCatalogElement?

    @Query("SELECT COUNT(id) FROM items")
    suspend fun itemsCount(): Int

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
