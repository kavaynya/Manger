package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.StatisticDao
import com.san.kir.data.db.main.entites.DbStatistic
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.main.SimplifiedStatistic
import com.san.kir.data.models.main.Statistic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public class StatisticsRepository internal constructor(private val statisticDao: StatisticDao) {
    public val allTime: Flow<Long> = statisticDao.loadAllTime()
    public val simplifiedItems: Flow<List<SimplifiedStatistic>> =
        statisticDao.loadSimpleItems().toModels()

    public suspend fun itemById(id: Long): Statistic? =
        withIoContext { statisticDao.itemById(id)?.toModel() }

    public fun item(itemId: Long = -1, mangaId: Long = -1): Flow<Statistic?> {
        if (itemId != -1L) return statisticDao.loadItemById(itemId).toModel()
        if (mangaId != -1L) return statisticDao.loadItemByMangaId(mangaId).toModel()
        return flowOf<Statistic?>(null)
    }

    public suspend fun save(item: Statistic): List<Long> =
        withIoContext { statisticDao.insert(item.toEntity()) }

    public suspend fun save(mangaId: Long): List<Long> =
        withIoContext { statisticDao.insert(DbStatistic(mangaId = mangaId)) }

    public suspend fun idByMangaId(mangaId: Long): Long? =
        withIoContext { statisticDao.idByMangaId(mangaId) }

    public suspend fun delete(itemId: Long): Unit = withIoContext { statisticDao.delete(itemId) }

    public suspend fun updateByChapter(chapter: Chapter): Unit = withIoContext {
        val item = statisticDao.itemByMangaId(chapter.mangaId) ?: return@withIoContext
        statisticDao.insert(
            item.copy(
                downloadSize = item.downloadSize + chapter.downloadSize,
                downloadTime = item.downloadTime + chapter.downloadTime
            )
        )
    }
}
