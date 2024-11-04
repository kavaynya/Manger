package com.san.kir.data.db.catalog.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.catalog.CatalogDb
import com.san.kir.data.db.catalog.dao.SiteCatalogDao
import com.san.kir.data.db.catalog.mappers.toEntities
import com.san.kir.data.db.catalog.mappers.toEntity
import com.san.kir.data.db.catalog.mappers.toModel
import com.san.kir.data.db.catalog.mappers.toModels
import com.san.kir.data.models.catalog.MiniCatalogItem
import com.san.kir.data.models.catalog.SiteCatalogElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class CatalogsRepository internal constructor(private val catalogFactory: (String) -> CatalogDb) {

    public fun loadItems(catalogName: String): Flow<List<MiniCatalogItem>> =
        catalogFactory(catalogName).dao.loadMinitItems().map { it.toModels() }

    public suspend fun items(catalogName: String): List<MiniCatalogItem> =
        use(catalogName) { miniItems().toModels() }

    public suspend fun item(catalogName: String, itemId: Long): SiteCatalogElement? =
        use(catalogName) { itemById(itemId)?.toModel() }

    public suspend fun volume(catalogName: String): Int = use(catalogName) { itemsCount() }

    public suspend fun save(catalogName: String, list: List<SiteCatalogElement>): Unit =
        use(catalogName) {
            var limit = 500
            var offset = 0

            val unics = mutableListOf<String>()

            val entities = list
                .filter { item ->
                    val notContain = unics.contains(item.link).not()
                    if (notContain) unics.add(item.link)
                    notContain
                }
                .toEntities()
                .toMutableList()

            do {
                val items = items(limit, offset).map {
                    var dbItem = it

                    entities.firstOrNull { it.link == dbItem.link }?.let { newItem ->
                        dbItem = dbItem.copy(
                            name = newItem.name,
                            logo = newItem.logo.ifBlank { dbItem.logo },
                            dateId = if (newItem.dateId == 0) dbItem.dateId else newItem.dateId,
                            populate = if (newItem.populate == 0) dbItem.populate else newItem.populate,
                            about = newItem.about.ifBlank { dbItem.about },
                            volume = maxOf(newItem.volume, dbItem.volume),
                            statusEdition = newItem.statusEdition.ifBlank { dbItem.statusEdition },
                            statusTranslate = newItem.statusTranslate.ifBlank { dbItem.statusTranslate },
                            authors = dbItem.authors.union(newItem.authors).toList(),
                            genres = dbItem.genres.union(newItem.genres).toList(),
                        )
                        entities.remove(newItem)
                    }

                    dbItem
                }
                insert(items)
                offset += limit
            } while (items.isNotEmpty())

            insert(entities)
        }

    public suspend fun insert(catalogName: String, item: SiteCatalogElement): List<Long> =
        use(catalogName) {
            val id = if (item.id > 0) {
                item.id
            } else {
                itemByLink(item.link)?.id ?: 0
            }
            insert(item.toEntity().copy(id = id))
        }

    private suspend fun <R> use(catalogName: String, action: suspend SiteCatalogDao.() -> R) =
        withIoContext {
            val db = catalogFactory(catalogName)
            val result = action.invoke(db.dao)
            db.close()
            result
        }
}
