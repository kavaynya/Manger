package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.dao.CatalogTaskDao
import com.san.kir.data.models.base.CatalogTask

class CatalogWorkerRepository(
    private val catalogDao: CatalogTaskDao,
) : BaseWorkerRepository<CatalogTask> {

    override val catalog = catalogDao.loadItems()

    override suspend fun remove(item: CatalogTask) {
        withIoContext { catalogDao.removeById(item.id) }
    }

    override suspend fun clear() = withIoContext { catalogDao.clear() }

    fun loadTask(name: String) = catalogDao.loadItemByName(name)
    suspend fun task(name: String) = withIoContext { catalogDao.itemByName(name) }
    suspend fun add(item: CatalogTask) = withIoContext { catalogDao.insert(item) }
    suspend fun update(item: CatalogTask) = withIoContext { catalogDao.update(item) }
}
