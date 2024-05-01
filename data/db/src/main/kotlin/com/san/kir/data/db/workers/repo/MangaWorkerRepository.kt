package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.MangaTaskDao
import com.san.kir.data.db.workers.entities.DbMangaTask
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.MangaTask
import kotlinx.coroutines.flow.Flow

class MangaWorkerRepository internal constructor(
    private val dao: MangaTaskDao
) : BaseWorkerRepository<MangaTask>(dao) {
    override val catalog = dao.loadItems().toModels()
    suspend fun remove(ids: List<Long>) = withIoContext { dao.removeByIds(ids) }
    fun loadTask(mangaId: Long) = dao.loadItemByMangaId(mangaId).toModel()
    suspend fun task(mangaId: Long) = withIoContext { dao.itemByMangaId(mangaId)?.toModel() }
    suspend fun save(item: MangaTask) = withIoContext { dao.insert(item.toEntity()) }

}
