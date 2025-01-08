package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.shortPath
import com.san.kir.data.db.main.dao.StorageDao
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class StorageRepository internal constructor(private val storageDao: StorageDao) {
    public val fullSize: Flow<Double> = storageDao.loadFullSize()
    public val fullSizeInt: Flow<Int> = fullSize.map(Double::toInt)
    public val items: Flow<List<Storage>> = storageDao.loadItems().toModels()

    public fun loadItemByPath(path: String): Flow<Storage?> =
        storageDao.loadItemByPath(getFullPath(path).shortPath).toModel()

    public suspend fun itemByPath(path: String): Storage? =
        storageDao.itemByPath(getFullPath(path).shortPath)?.toModel()

    public suspend fun items(): List<Storage> = storageDao.items().toModels()
    public suspend fun item(id: Long): Storage? = storageDao.itemById(id)?.toModel()
    public suspend fun save(item: Storage): List<Long> = storageDao.insert(item.toEntity())
    public suspend fun delete(id: Long): Int = storageDao.delete(id)
    public suspend fun delete(item: Storage): Int = storageDao.delete(item.toEntity())
    public suspend fun delete(items: List<Storage>): Int = storageDao.delete(items.toEntities())
}
