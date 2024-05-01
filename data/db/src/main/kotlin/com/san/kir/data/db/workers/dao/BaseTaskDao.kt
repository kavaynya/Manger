package com.san.kir.data.db.workers.dao

import com.san.kir.data.db.base.BaseDao
import kotlinx.coroutines.flow.Flow

interface BaseTaskDao<T> : BaseDao<T> {
    fun loadItems(): Flow<List<T>>
    suspend fun removeById(id: Long)
    suspend fun clear()
}
