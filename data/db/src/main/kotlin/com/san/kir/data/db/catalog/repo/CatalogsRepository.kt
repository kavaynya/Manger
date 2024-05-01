package com.san.kir.data.db.catalog.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.catalog.CatalogDb
import com.san.kir.data.db.catalog.dao.SiteCatalogDao
import com.san.kir.data.db.catalog.mappers.toEntities
import com.san.kir.data.db.catalog.mappers.toEntity
import com.san.kir.data.db.catalog.mappers.toModel
import com.san.kir.data.db.catalog.mappers.toModels
import com.san.kir.data.models.catalog.SiteCatalogElement

class CatalogsRepository internal constructor(private val catalogFactory: (String) -> CatalogDb) {

    suspend fun items(catalogName: String) = use(catalogName) { miniItems().toModels() }

    suspend fun item(catalogName: String, itemId: Long) =
        use(catalogName) { itemById(itemId)?.toModel() }

    suspend fun volume(catalogName: String) = use(catalogName) { itemsCount() }

    suspend fun save(catalogName: String, list: List<SiteCatalogElement>) = use(catalogName) {
        deleteAll()

        val unics = mutableListOf<String>()
        val entities = list.filter { item ->
            val notContain = unics.contains(item.link).not()
            if (notContain) unics.add(item.link)
            notContain
        }.toEntities()

        insert(entities)
    }

    suspend fun insert(catalogName: String, item: SiteCatalogElement) = use(catalogName) {
        insert(item.toEntity())
    }

    private suspend fun <R> use(catalogName: String, action: suspend SiteCatalogDao.() -> R) =
        withIoContext {
            val db = catalogFactory(catalogName)
            val result = action.invoke(db.dao)
            db.close()
            result
        }
}
