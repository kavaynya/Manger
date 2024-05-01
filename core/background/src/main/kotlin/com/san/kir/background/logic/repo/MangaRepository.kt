package com.san.kir.background.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.ChapterDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.entites.DbChapter
import com.san.kir.data.db.main.entites.DbManga

class MangaRepository(
    private val mangaDao: MangaDao,
    private val chapterDao: ChapterDao,
) {
    suspend fun manga(mangaId: Long) = withIoContext { mangaDao.itemById(mangaId) }
    suspend fun add(chapter: DbChapter) = withIoContext { chapterDao.insert(chapter) }
    suspend fun add(chapters: List<DbChapter>) = withIoContext { chapterDao.insert(chapters) }
    suspend fun chapters(manga: DbManga) = withIoContext { chapterDao.itemsByMangaId(manga.id) }
    suspend fun update(chapter: DbChapter) = withIoContext { chapterDao.update(chapter) }
    suspend fun update(chapters: List<DbChapter>) = withIoContext { chapterDao.update(chapters) }
    suspend fun delete(chapters: List<DbChapter>) = withIoContext { chapterDao.delete(chapters) }
    suspend fun deleteByIds(chapters: List<Long>) =
        withIoContext { chapterDao.deleteByIds(chapters) }
}
