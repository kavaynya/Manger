package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.CatalogTaskDao
import com.san.kir.data.db.workers.dao.ChapterTaskDao
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.CatalogTask
import com.san.kir.data.models.workers.ChapterTask

class ChapterWorkerRepository internal constructor(
    private val dao: ChapterTaskDao
) : BaseWorkerRepository<ChapterTask>(dao) {

    override val catalog = dao.loadItems().toModels()
    fun loadTask(chapterId: Long) = dao.loadItemByChapterId(chapterId).toModel()
    suspend fun task(chapterId: Long) = withIoContext { dao.itemByChapterId(chapterId)?.toModel() }
    suspend fun save(item: ChapterTask) = withIoContext { dao.insert(item.toEntity()) }

}
