package com.san.kir.manger.room.dao

import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.san.kir.manger.room.models.LatestChapter
import com.san.kir.manger.room.models.action
import com.san.kir.manger.room.models.isRead
import com.san.kir.manger.utils.ChapterStatus
import kotlinx.coroutines.experimental.async

@Dao
interface LatestChapterDao : BaseDao<LatestChapter> {
    @Query("SELECT * FROM latestChapters ORDER BY id DESC")
    fun loadLatestChapters(): DataSource.Factory<Int, LatestChapter>

    @Query("SELECT * FROM latestChapters")
    fun load(): List<LatestChapter>

    @Query("SELECT * FROM latestChapters WHERE site IS :link")
    fun loadChaptersWhereLink(link: String): List<LatestChapter>

    @Query("SELECT * FROM latestChapters WHERE manga IS :manga")
    fun loadChaptersWhereManga(manga: String): List<LatestChapter>
}

fun LatestChapterDao.removeChapters(manga: String) =
    delete(*loadChaptersWhereManga(manga).toTypedArray())

fun LatestChapterDao.clearHistoryDownload() = async {
    load().filter { it.action == ChapterStatus.DELETE }.forEach { deleteAsync(it) }
}

fun LatestChapterDao.clearHistoryRead() = async {
    load().filter { it.isRead.await() }.forEach { deleteAsync(it) }
}

fun LatestChapterDao.clearHistory() = async {
    load().forEach { deleteAsync(it) }
}

fun LatestChapterDao.downloadNewChapters() = async {
    load().filter { !it.isRead.await() }
        .filter { it.action == ChapterStatus.DOWNLOADABLE }
}

fun LatestChapterDao.hasNewChapters() = async {
    load().filter { !it.isRead.await() }
        .any { it.action == ChapterStatus.DOWNLOADABLE }
}

fun LatestChapterDao.loadPagedLatestChapters() =
    LivePagedListBuilder(loadLatestChapters(), 20).build()