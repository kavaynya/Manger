package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.dao.MangaTaskDao
import com.san.kir.data.models.base.MangaTask

class MangaWorkerRepository(
    private val mangaDao: MangaTaskDao,
) : BaseWorkerRepository<MangaTask> {

    override val catalog = mangaDao.loadItems()

    override suspend fun remove(item: MangaTask) {
        withIoContext { mangaDao.removeById(item.id) }
    }

    suspend fun remove(ids: List<Long>) = withIoContext { mangaDao.removeByIds(ids) }

    override suspend fun clear() = withIoContext { mangaDao.clear() }

    fun loadTask(mangaId: Long) = mangaDao.loadItemByMangaId(mangaId)
    suspend fun task(mangaId: Long) = withIoContext { mangaDao.itemByMangaId(mangaId) }
    suspend fun add(item: MangaTask) = withIoContext { mangaDao.insert(item) }
    suspend fun update(item: MangaTask) = withIoContext { mangaDao.update(item) }
}
