package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.MangaTaskDao
import com.san.kir.data.db.workers.entities.DbMangaTask

class MangaWorkerRepository(
    private val mangaDao: MangaTaskDao,
) : BaseWorkerRepository<DbMangaTask> {

    override val catalog = mangaDao.loadItems()

    override suspend fun remove(item: DbMangaTask) {
        withIoContext { mangaDao.removeById(item.id) }
    }

    suspend fun remove(ids: List<Long>) = withIoContext { mangaDao.removeByIds(ids) }

    override suspend fun clear() = withIoContext { mangaDao.clear() }

    fun loadTask(mangaId: Long) = mangaDao.loadItemByMangaId(mangaId)
    suspend fun task(mangaId: Long) = withIoContext { mangaDao.itemByMangaId(mangaId) }
    suspend fun add(item: DbMangaTask) = withIoContext { mangaDao.insert(item) }
    suspend fun update(item: DbMangaTask) = withIoContext { mangaDao.update(item) }
}
