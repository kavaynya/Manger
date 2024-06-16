package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.catalog.SiteCatalogElement
import com.san.kir.data.models.main.Manga
import com.san.kir.data.models.main.MangaLogo
import com.san.kir.data.models.main.MangaWithChaptersCount
import com.san.kir.data.models.main.MiniManga
import com.san.kir.data.models.main.NameAndId
import com.san.kir.data.models.main.SimplifiedManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File

public class MangaRepository internal constructor(private val mangaDao: MangaDao) {
    public val count: Flow<Int> = mangaDao.loadItemsCount()
    public val items: Flow<List<Manga>> = mangaDao.loadItems().toModels()
    public val itemsWithChaptersCount: Flow<List<MangaWithChaptersCount>> =
        mangaDao.loadItemsWithChapterCounts().toModels()
    public val miniItems: Flow<List<MiniManga>> = mangaDao.loadMiniItems().toModels()
    public val namesAndIds: Flow<List<NameAndId>> = mangaDao.loadNamesAndIds().toModels()
    public val simplifiedItems: Flow<List<SimplifiedManga>> =
        mangaDao.loadSimpleItems().distinctUntilChanged().toModels()

    public fun loadItem(mangaId: Long): Flow<Manga?> = mangaDao.loadItemById(mangaId).toModel()

    public fun loadItemWithChaptersCount(mangaId: Long): Flow<MangaWithChaptersCount?> =
        mangaDao.loadItemWithChapterCountsById(mangaId).toModel()

    public suspend fun idsByNames(names: List<String>): List<Long> =
        withIoContext { mangaDao.itemIdsByNames(names) }

    public suspend fun idsByCategoryId(id: Long): List<Long> =
        withIoContext { mangaDao.itemIdsByCategoryId(id) }

    public suspend fun ids(): List<Long> = withIoContext { mangaDao.itemIds(false) }
    public suspend fun item(mangaId: Long): Manga? =
        withIoContext { mangaDao.itemById(mangaId)?.toModel() }

    public suspend fun items(): List<Manga> = withIoContext { mangaDao.items().toModels() }
    public suspend fun itemByPath(path: String): MangaLogo? = itemByPath(getFullPath(path))
    public suspend fun itemByPath(file: File): MangaLogo? = withIoContext {
        mangaDao.specItems().firstOrNull { getFullPath(it.path) == file }?.toModel()
    }

    public suspend fun changeCategory(mangaId: Long, newCategoryId: Long): Unit =
        withIoContext { mangaDao.updateCategory(mangaId, newCategoryId) }

    public suspend fun changeColor(mangaId: Long, newColor: Int): Unit =
        withIoContext { mangaDao.updateColor(mangaId, newColor) }

    public suspend fun changeIsUpdate(mangaId: Long, update: Boolean): Unit =
        withIoContext { mangaDao.updateIsUpdate(mangaId, update) }

    public suspend fun name(mangaId: Long): String? =
        withIoContext { mangaDao.itemById(mangaId)?.name }

    public suspend fun save(item: Manga): List<Long> =
        withIoContext { mangaDao.insert(item.toEntity()) }

    public suspend fun save(items: List<Manga>): List<Long> =
        withIoContext { mangaDao.insert(items.toEntities()) }

    public suspend fun delete(item: Manga): Int = withIoContext { mangaDao.delete(item.toEntity()) }
    public suspend fun itemsByCategoryId(id: Long): List<Manga> =
        withIoContext { mangaDao.itemsByCategoryId(id).toModels() }

    public suspend fun updateMangaBy(item: SiteCatalogElement): Unit = withIoContext {
        mangaDao.items().forEach { dbItem ->
            if (dbItem.shortLink.contains(item.shortLink)) {
                mangaDao.insert(
                    dbItem.copy(
                        host = item.host,
                        logo = item.logo,
                        about = item.about,
                        status = item.statusEdition,
                        shortLink = item.shortLink,
                        authorsList = item.authors,
                        genresList = item.genres
                    )
                )
            }
        }
    }
}
