package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.PlannedDao
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.PlannedTask
import com.san.kir.data.models.main.SimplifiedTask
import kotlinx.coroutines.flow.Flow

public class PlannedRepository internal constructor(private val plannedDao: PlannedDao) {
    public val count: Flow<Int> = plannedDao.loadItemsCount()
    public val simplifiedItems: Flow<List<SimplifiedTask>> = plannedDao.loadSimpleItems().toModels()
    public suspend fun item(itemId: Long): PlannedTask? =
        withIoContext { plannedDao.itemById(itemId)?.toModel() }

    public suspend fun update(id: Long, enable: Boolean): Unit =
        withIoContext { plannedDao.update(id, enable) }

    public suspend fun save(item: PlannedTask): List<Long> =
        withIoContext { plannedDao.insert(item.toEntity()) }
}
