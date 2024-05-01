package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.BaseTaskDao
import com.san.kir.data.models.base.BaseTask
import kotlinx.coroutines.flow.Flow

abstract class BaseWorkerRepository<OUT : BaseTask<OUT>>(
    private val dao: BaseTaskDao<*>,
) {
   abstract val catalog: Flow<List<OUT>>
    suspend fun remove(item: OUT) = withIoContext { dao.removeById(item.id) }
    suspend fun clear() = withIoContext { dao.clear() }

}
