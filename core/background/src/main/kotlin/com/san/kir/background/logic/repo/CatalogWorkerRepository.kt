package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.CatalogTaskDao
import com.san.kir.data.db.workers.entities.DbCatalogTask

class CatalogWorkerRepository(
    private val catalogDao: CatalogTaskDao,
) : BaseWorkerRepository<DbCatalogTask> {

    override val catalog = catalogDao.loadItems()

    override suspend fun remove(item: DbCatalogTask) {
        withIoContext { catalogDao.removeById(item.id) }
    }

    override suspend fun clear() = withIoContext { catalogDao.clear() }

    fun loadTask(name: String) = catalogDao.loadItemByName(name)
    suspend fun task(name: String) = withIoContext { catalogDao.itemByName(name) }
    suspend fun add(item: DbCatalogTask) = withIoContext { catalogDao.insert(item) }
    suspend fun update(item: DbCatalogTask) = withIoContext { catalogDao.update(item) }
}
