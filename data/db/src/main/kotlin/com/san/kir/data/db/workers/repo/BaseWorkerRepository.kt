package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.BaseTaskDao
import com.san.kir.data.models.base.BaseTask
import kotlinx.coroutines.flow.Flow

public abstract class BaseWorkerRepository<OUT : BaseTask<OUT>> internal constructor(
    private val dao: BaseTaskDao<*>,
) {
    public abstract val catalog: Flow<List<OUT>>
    public suspend fun remove(item: OUT): Unit = withIoContext { dao.removeById(item.id) }
    public suspend fun clear(): Unit = withIoContext { dao.clear() }

}
