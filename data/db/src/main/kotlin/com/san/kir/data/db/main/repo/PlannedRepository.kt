package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.PlannedDao
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.PlannedTask

class PlannedRepository internal constructor(private val plannedDao: PlannedDao) {
    val count = plannedDao.loadItemsCount()
    val simplifiedItems = plannedDao.loadSimpleItems().toModels()
    suspend fun item(itemId: Long) = withIoContext { plannedDao.itemById(itemId)?.toModel() }
    suspend fun update(id: Long, enable: Boolean) = withIoContext { plannedDao.update(id, enable) }

    suspend fun save(item: PlannedTask) = withIoContext { plannedDao.insert(item.toEntity()) }
}
