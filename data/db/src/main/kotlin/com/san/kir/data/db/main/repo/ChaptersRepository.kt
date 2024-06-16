package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.main.DownloadItem
import com.san.kir.data.models.main.SimplifiedChapter
import com.san.kir.data.models.utils.ChapterComparator
import com.san.kir.data.models.utils.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class ChapterRepository internal constructor(private val chapterDao: ChapterDao) {
    public val downloadCount: Flow<Int> = chapterDao.loadDownloadCount()
    public val latestCount: Flow<Int> = chapterDao.loadLatestCount()
    public val simplifiedItems: Flow<List<SimplifiedChapter>> =
        chapterDao.loadSimpleItems().toModels()
    public val downloadItems: Flow<List<DownloadItem>> = chapterDao.loadItemsByNotStatus()
        .map { items -> items.sortedBy { it.status.ordinal } }
        .toModels()

    public fun items(mangaId: Long): Flow<List<SimplifiedChapter>> =
        chapterDao.loadSimpleItemsByMangaId(mangaId).toModels()

    public suspend fun save(item: Chapter): List<Long> =
        withIoContext { chapterDao.insert(item.toEntity()) }

    public suspend fun save(items: List<Chapter>): List<Long> =
        withIoContext { chapterDao.insert(items.toEntities()) }

    public suspend fun updateIsRead(ids: List<Long>, isRead: Boolean): Unit =
        withIoContext { chapterDao.updateIsRead(ids, isRead) }

    public suspend fun updateIsInUpdate(ids: Iterable<Long>, isInUpdate: Boolean): Unit =
        withIoContext { chapterDao.updateIsInUpdate(ids.toList(), isInUpdate) }

    public suspend fun updateFor(status: DownloadState): Unit =
        withIoContext { chapterDao.updateChapters(status) }

    public suspend fun reset(mangaId: Long): Unit =
        withIoContext { chapterDao.fullReadingReset(mangaId) }

    public suspend fun reset(ids: List<Long>): Unit = withIoContext { chapterDao.readingReset(ids) }
    public suspend fun item(id: Long): Chapter = withIoContext { chapterDao.itemById(id).toModel() }
    public suspend fun delete(ids: List<Long>): Int = withIoContext { chapterDao.deleteByIds(ids) }
    public suspend fun allItems(): List<Chapter> = withIoContext { chapterDao.items().toModels() }
    public suspend fun allItems(mangaId: Long): List<Chapter> =
        withIoContext { chapterDao.itemsByMangaId(mangaId).toModels() }

    public suspend fun notReadItems(mangaId: Long): List<Chapter> =
        withIoContext { chapterDao.itemsNotReadByMangaId(mangaId).toModels() }

    public suspend fun setReadFirst(
        mangaId: Long,
        isAlternativeSort: Boolean,
        count: Int
    ): List<Long> = withIoContext {
        var items = chapterDao.itemsByMangaId(mangaId).toModels()
        if (isAlternativeSort) {
            items = items.sortedWith(ChapterComparator())
        }
        val insertedItems = items.take(count).map { it.copy(isRead = true) }.toEntities()
        chapterDao.insert(insertedItems)
    }

    public suspend fun newItem(mangaId: Long): Chapter? =
        withIoContext { chapterDao.itemsNotReadByMangaId(mangaId).firstOrNull()?.toModel() }

    public suspend fun mangaIdById(chapterId: Long): Long =
        withIoContext { chapterDao.mangaIdById(chapterId) }

    public suspend fun addToQueue(chapterId: Long): Unit =
        withIoContext { chapterDao.setQueueStatus(chapterId) }

    public suspend fun pauseChapters(ids: List<Long>): Unit =
        withIoContext { chapterDao.updateStatus(ids, DownloadState.PAUSED) }

    public suspend fun pausedChapters(): List<Chapter> =
        withIoContext { chapterDao.itemsByStatus(DownloadState.PAUSED).toModels() }
}
