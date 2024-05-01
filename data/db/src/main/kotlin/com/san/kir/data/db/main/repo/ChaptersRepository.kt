package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.utils.ChapterComparator
import com.san.kir.data.models.utils.DownloadState
import kotlinx.coroutines.flow.map

class ChapterRepository internal constructor(private val chapterDao: ChapterDao) {
    val downloadCount = chapterDao.loadDownloadCount()
    val latestCount = chapterDao.loadLatestCount()
    val simplifiedItems = chapterDao.loadSimpleItems().toModels()
    val downloadItems = chapterDao.loadItemsByNotStatus()
        .map { items -> items.sortedBy { it.status.ordinal } }
        .toModels()

    fun items(mangaId: Long) = chapterDao.loadSimpleItemsByMangaId(mangaId).toModels()
    suspend fun insert(item: Chapter) = withIoContext { chapterDao.insert(item.toEntity()) }
    suspend fun insert(items: List<Chapter>) =
        withIoContext { chapterDao.insert(items.toEntities()) }

    suspend fun updateIsRead(ids: List<Long>, isRead: Boolean) =
        withIoContext { chapterDao.updateIsRead(ids, isRead) }

    suspend fun updateIsInUpdate(ids: Iterable<Long>, isInUpdate: Boolean) =
        withIoContext { chapterDao.updateIsInUpdate(ids.toList(), isInUpdate) }

    suspend fun updateFor(status: DownloadState) =
        withIoContext { chapterDao.updateChapters(status) }

    suspend fun reset(mangaId: Long) = withIoContext { chapterDao.fullReadingReset(mangaId) }
    suspend fun reset(ids: List<Long>) = withIoContext { chapterDao.readingReset(ids) }
    suspend fun item(id: Long) = withIoContext { chapterDao.itemById(id).toModel() }
    suspend fun delete(ids: List<Long>) = withIoContext { chapterDao.deleteByIds(ids) }
    suspend fun allItems() = withIoContext { chapterDao.items().toModels() }
    suspend fun allItems(mangaId: Long) =
        withIoContext { chapterDao.itemsByMangaId(mangaId).toModels() }

    suspend fun notReadItems(mangaId: Long) =
        withIoContext { chapterDao.itemsNotReadByMangaId(mangaId).toModels() }

    suspend fun setReadFirst(mangaId: Long, isAlternativeSort: Boolean, count: Int) =
        withIoContext {
            var items = chapterDao.itemsByMangaId(mangaId).toModels()
            if (isAlternativeSort) {
                items = items.sortedWith(ChapterComparator())
            }
            val insertedItems = items.take(count).map { it.copy(isRead = true) }.toEntities()
            chapterDao.insert(insertedItems)
        }

    suspend fun newItem(mangaId: Long) =
        withIoContext { chapterDao.itemsNotReadByMangaId(mangaId).firstOrNull()?.toModel() }

    suspend fun mangaIdById(chapterId: Long) = withIoContext { chapterDao.mangaIdById(chapterId) }

    suspend fun addToQueue(chapterId: Long) = withIoContext { chapterDao.setQueueStatus(chapterId) }

    suspend fun pauseChapters(ids: List<Long>) =
        withIoContext { chapterDao.updateStatus(ids, DownloadState.PAUSED) }

    suspend fun pausedChapters() =
        withIoContext { chapterDao.itemsByStatus(DownloadState.PAUSED).toModels() }
}
