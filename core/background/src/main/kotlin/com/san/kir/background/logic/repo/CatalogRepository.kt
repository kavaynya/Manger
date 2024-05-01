package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.catalog.CatalogDb
import com.san.kir.data.db.catalog.entities.DbSiteCatalogElement
import com.san.kir.data.parsing.SiteCatalogsManager

class CatalogRepository(
    private val catalogFactory: (String) -> CatalogDb,
    private val manager: SiteCatalogsManager,
) {
    suspend fun save(name: String, items: List<DbSiteCatalogElement>) = withIoContext {
        catalogFactory(manager.catalogName(name)).apply {
            dao.deleteAll()
            dao.insert(*items.toTypedArray())
            close()
        }
    }
}
