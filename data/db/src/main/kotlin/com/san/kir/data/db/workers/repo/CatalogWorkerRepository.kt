package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.CatalogTaskDao
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.CatalogTask
import kotlinx.coroutines.flow.Flow

public class CatalogWorkerRepository internal constructor(
    private val dao: CatalogTaskDao
) : BaseWorkerRepository<CatalogTask>(dao) {
    override val catalog: Flow<List<CatalogTask>> = dao.loadItems().toModels()
    public fun loadTask(name: String): Flow<CatalogTask?> = dao.loadItemByName(name).toModel()
    public suspend fun task(name: String): CatalogTask? =
        withIoContext { dao.itemByName(name)?.toModel() }

    public suspend fun save(item: CatalogTask): List<Long> =
        withIoContext { dao.insert(item.toEntity()) }

}
