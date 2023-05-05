package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.dao.ChapterTaskDao
import com.san.kir.data.models.base.ChapterTask

class ChapterWorkerRepository(
    private val chapterDao: ChapterTaskDao,
) : BaseWorkerRepository<ChapterTask> {

    override val catalog = chapterDao.loadItems()

    override suspend fun remove(item: ChapterTask) {
        withIoContext { chapterDao.removeById(item.id) }
    }

    override suspend fun clear() = withIoContext { chapterDao.clear() }

    fun loadTask(chapterId: Long) = chapterDao.loadItemByChapterId(chapterId)
    suspend fun task(chapterId: Long) = withIoContext { chapterDao.itemByChapterId(chapterId) }
    suspend fun add(item: ChapterTask) = withIoContext { chapterDao.insert(item) }
    suspend fun update(item: ChapterTask) = withIoContext { chapterDao.update(item) }
    suspend fun remove(items: List<ChapterTask>) =
        withIoContext { chapterDao.removeByIds(items.map { it.id }) }
}
