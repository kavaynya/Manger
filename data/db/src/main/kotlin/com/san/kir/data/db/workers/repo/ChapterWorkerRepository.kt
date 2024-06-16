package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.ChapterTaskDao
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.ChapterTask
import kotlinx.coroutines.flow.Flow

public class ChapterWorkerRepository internal constructor(
    private val dao: ChapterTaskDao
) : BaseWorkerRepository<ChapterTask>(dao) {

    override val catalog: Flow<List<ChapterTask>> = dao.loadItems().toModels()
    public fun loadTask(chapterId: Long): Flow<ChapterTask?> =
        dao.loadItemByChapterId(chapterId).toModel()

    public suspend fun task(chapterId: Long): ChapterTask? =
        withIoContext { dao.itemByChapterId(chapterId)?.toModel() }

    public suspend fun save(item: ChapterTask): List<Long> =
        withIoContext { dao.insert(item.toEntity()) }

    public suspend fun remove(items: List<ChapterTask>): Unit =
        withIoContext { dao.removeByIds(items.map { it.id }) }

}
