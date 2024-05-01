package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.dao.ChapterTaskDao
import com.san.kir.data.db.workers.entities.DbChapterTask

class ChapterWorkerRepository(
    private val chapterDao: ChapterTaskDao,
) : BaseWorkerRepository<DbChapterTask> {

    override val catalog = chapterDao.loadItems()

    override suspend fun remove(item: DbChapterTask) {
        withIoContext { chapterDao.removeById(item.id) }
    }

    override suspend fun clear() = withIoContext { chapterDao.clear() }

    fun loadTask(chapterId: Long) = chapterDao.loadItemByChapterId(chapterId)
    suspend fun task(chapterId: Long) = withIoContext { chapterDao.itemByChapterId(chapterId) }
    suspend fun add(item: DbChapterTask) = withIoContext { chapterDao.insert(item) }
    suspend fun update(item: DbChapterTask) = withIoContext { chapterDao.update(item) }
    suspend fun remove(items: List<DbChapterTask>) =
        withIoContext { chapterDao.removeByIds(items.map { it.id }) }
}
