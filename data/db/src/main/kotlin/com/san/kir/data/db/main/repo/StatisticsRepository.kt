package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.StatisticDao
import com.san.kir.data.db.main.entites.DbStatistic
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.main.Statistic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class StatisticsRepository internal constructor(private val statisticDao: StatisticDao) {
    val allTime = statisticDao.loadAllTime()
    val simplifiedItems = statisticDao.loadSimpleItems().toModels()

    suspend fun itemById(id: Long) = withIoContext { statisticDao.itemById(id)?.toModel() }

    fun item(itemId: Long?, mangaId: Long?): Flow<Statistic?> {
        if (itemId != null) return statisticDao.loadItemById(itemId).toModel()
        if (mangaId != null) return statisticDao.loadItemByMangaId(mangaId).toModel()
        return flowOf<Statistic?>(null)
    }

    suspend fun insert(item: Statistic) = withIoContext { statisticDao.insert(item.toEntity()) }

    suspend fun insert(mangaId: Long) =
        withIoContext { statisticDao.insert(DbStatistic(mangaId = mangaId)) }

    suspend fun idByMangaId(mangaId: Long) = withIoContext { statisticDao.idByMangaId(mangaId) }
    suspend fun delete(itemId: Long) = withIoContext { statisticDao.delete(itemId) }

    suspend fun updateByChapter(chapter: Chapter) = withIoContext {
        val item = statisticDao.itemByMangaId(chapter.mangaId) ?: return@withIoContext
        statisticDao.insert(
            item.copy(
                downloadSize = item.downloadSize + chapter.downloadSize,
                downloadTime = item.downloadTime + chapter.downloadTime
            )
        )
    }
}
