package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.custom.DbDownloadChapter
import com.san.kir.data.db.main.entites.DbChapter
import com.san.kir.data.db.main.views.ViewChapter
import com.san.kir.data.models.utils.DownloadState
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ChapterDao : BaseDao<DbChapter> {
    @Query("SELECT * FROM simple_chapter")
    fun loadSimpleItems(): Flow<List<ViewChapter>>

    @Query(
        "SELECT id, status, progress, isRead, downloadPages, pages, name, '' AS manga, date, path, added_timestamp " +
                "FROM chapters WHERE manga_id=:mangaId"
    )
    fun loadSimpleItemsByMangaId(mangaId: Long): Flow<List<ViewChapter>>

    @Query("SELECT * FROM chapters WHERE isInUpdate=1 AND isRead=0")
    fun loadNotReadItems(): Flow<List<DbChapter>>

    @Query("SELECT COUNT(id) FROM simple_chapter")
    fun loadLatestCount(): Flow<Int>

    @Query("SELECT COUNT(id) FROM chapters WHERE status=:queued OR status=:loading")
    fun loadDownloadCount(
        queued: DownloadState = DownloadState.QUEUED,
        loading: DownloadState = DownloadState.LOADING,
    ): Flow<Int>

    @Query(
        "SELECT chapters.id, chapters.name, manga.name AS manga, manga.logo AS logo, " +
                "chapters.status, chapters.totalTime, chapters.downloadSize, chapters.downloadPages, " +
                "chapters.pages " +
                "FROM chapters JOIN manga ON chapters.manga_id=manga.id " +
                "WHERE chapters.status IS NOT :status " +
                "ORDER BY chapters.ordering"
    )
    fun loadItemsByNotStatus(status: DownloadState = DownloadState.UNKNOWN): Flow<List<DbDownloadChapter>>

    @Query("SELECT * FROM chapters")
    suspend fun items(): List<DbChapter>

    @Query("SELECT * FROM chapters WHERE manga_id IS :mangaId")
    suspend fun itemsByMangaId(mangaId: Long): List<DbChapter>

    @Query("SELECT * FROM chapters WHERE status IS :status ORDER BY ordering")
    suspend fun itemsByStatus(status: DownloadState): List<DbChapter>

    @Query("SELECT * FROM chapters WHERE manga_id IS :mangaId AND isRead IS 0 ORDER BY id ASC")
    suspend fun itemsNotReadByMangaId(mangaId: Long): List<DbChapter>

    @Query("SELECT * FROM chapters WHERE id IS :id")
    suspend fun itemById(id: Long): DbChapter

    @Query("SELECT manga_id FROM chapters WHERE id IS :id")
    suspend fun mangaIdById(id: Long): Long

    @Query("UPDATE chapters SET isInUpdate=:isInUpdate WHERE id IN (:ids)")
    suspend fun updateIsInUpdate(ids: List<Long>, isInUpdate: Boolean)

    @Query("UPDATE chapters SET isRead=:readStatus WHERE id IN (:ids)")
    suspend fun updateIsRead(ids: List<Long>, readStatus: Boolean)

    @Query("UPDATE chapters SET status=:status WHERE id IN (:ids)")
    suspend fun updateStatus(ids: List<Long>, status: DownloadState = DownloadState.UNKNOWN)

    @Query("UPDATE chapters SET status=:status, ordering=:time WHERE id IS :id")
    suspend fun setQueueStatus(
        id: Long,
        status: DownloadState = DownloadState.QUEUED,
        time: Long = System.currentTimeMillis(),
    )

    @Query("DELETE FROM chapters WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>): Int

    @Query("UPDATE chapters SET status=:newStatus WHERE status=:oldStatus")
    suspend fun updateChapters(oldStatus: DownloadState, newStatus: DownloadState = DownloadState.UNKNOWN)

    @Query("UPDATE chapters SET progress=0, isRead=0 WHERE id IN (:ids)")
    suspend fun readingReset(ids: List<Long>)

    @Query("UPDATE chapters SET progress=0, isRead=0 WHERE manga_id = :mangaId")
    suspend fun fullReadingReset(mangaId: Long)
}

