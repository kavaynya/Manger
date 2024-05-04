package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.db.main.dao.StorageDao
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Storage
import kotlinx.coroutines.flow.map

class StorageRepository internal constructor(private val storageDao: StorageDao) {
    val fullSize = storageDao.loadFullSize()
    val fullSizeInt = fullSize.map(Double::toInt)
    val items = storageDao.loadItems().toModels()

    fun loadItemByPath(path: String) =
        storageDao.loadItemByPath(getFullPath(path).shortPath).toModel()

    suspend fun itemByPath(path: String) =
        withIoContext { storageDao.itemByPath(getFullPath(path).shortPath)?.toModel() }

    suspend fun items() = withIoContext { storageDao.items().toModels() }
    suspend fun save(item: Storage) = withIoContext { storageDao.insert(item.toEntity()) }
    suspend fun delete(item: Storage) = withIoContext { storageDao.delete(item.toEntity()) }
    suspend fun delete(items: List<Storage>) =
        withIoContext { storageDao.delete(items.toEntities()) }
}
