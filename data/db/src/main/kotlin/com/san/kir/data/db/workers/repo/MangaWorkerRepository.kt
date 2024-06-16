package com.san.kir.data.db.workers.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.MangaTaskDao
import com.san.kir.data.db.workers.entities.DbMangaTask
import com.san.kir.data.db.workers.mappers.toEntity
import com.san.kir.data.db.workers.mappers.toModel
import com.san.kir.data.db.workers.mappers.toModels
import com.san.kir.data.models.workers.MangaTask
import kotlinx.coroutines.flow.Flow

public class MangaWorkerRepository internal constructor(
    private val dao: MangaTaskDao
) : BaseWorkerRepository<MangaTask>(dao) {
    override val catalog: Flow<List<MangaTask>> = dao.loadItems().toModels()
    public suspend fun remove(ids: List<Long>): Unit = withIoContext { dao.removeByIds(ids) }
    public fun loadTask(mangaId: Long): Flow<MangaTask?> = dao.loadItemByMangaId(mangaId).toModel()
    public suspend fun task(mangaId: Long): MangaTask? =
        withIoContext { dao.itemByMangaId(mangaId)?.toModel() }

    public suspend fun save(item: MangaTask): List<Long> =
        withIoContext { dao.insert(item.toEntity()) }

}
