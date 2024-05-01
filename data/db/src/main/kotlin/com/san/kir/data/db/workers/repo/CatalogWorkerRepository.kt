package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.CatalogTaskDao
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.CatalogTask

class CatalogWorkerRepository internal constructor(
    private val dao: CatalogTaskDao
) : BaseWorkerRepository<CatalogTask>(dao) {
    override val catalog = dao.loadItems().toModels()
    fun loadTask(name: String) = dao.loadItemByName(name).toModel()
    suspend fun task(name: String) = withIoContext { dao.itemByName(name)?.toModel() }
    suspend fun save(item: CatalogTask) = withIoContext { dao.insert(item.toEntity()) }

}
