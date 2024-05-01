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
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File

class MangaRepository internal constructor(private val mangaDao: MangaDao) {
    val count = mangaDao.loadItemsCount()
    val items = mangaDao.loadItems().toModels()
    val itemsWithChaptersCount = mangaDao.loadItemsWithChapterCounts().toModels()
    val miniItems = mangaDao.loadMiniItems().toModels()
    val namesAndIds = mangaDao.loadNamesAndIds().toModels()
    val simplifiedItems = mangaDao.loadSimpleItems().distinctUntilChanged().toModels()
    fun loadItem(mangaId: Long) = mangaDao.loadItemById(mangaId).toModel()

    fun loadItemWithChaptersCount(mangaId: Long) =
        mangaDao.loadItemWithChapterCountsById(mangaId).toModel()

    suspend fun idsByNames(names: List<String>) = withIoContext { mangaDao.itemIdsByNames(names) }
    suspend fun idsByCategoryId(id: Long) = withIoContext { mangaDao.itemIdsByCategoryId(id) }
    suspend fun ids() = withIoContext { mangaDao.itemIds(false) }
    suspend fun item(mangaId: Long) = withIoContext { mangaDao.itemById(mangaId)?.toModel() }
    suspend fun items() = withIoContext { mangaDao.items().toModels() }
    suspend fun itemByPath(path: String) = itemByPath(getFullPath(path))
    suspend fun itemByPath(file: File) = withIoContext {
        mangaDao.specItems().firstOrNull { getFullPath(it.path) == file }?.toModel()
    }

    suspend fun changeCategory(mangaId: Long, newCategoryId: Long) =
        withIoContext { mangaDao.updateCategory(mangaId, newCategoryId) }

    suspend fun changeColor(mangaId: Long, newColor: Int) =
        withIoContext { mangaDao.updateColor(mangaId, newColor) }

    suspend fun changeIsUpdate(mangaId: Long, update: Boolean) =
        withIoContext { mangaDao.updateIsUpdate(mangaId, update) }

    suspend fun name(mangaId: Long) = withIoContext { mangaDao.itemById(mangaId)?.name }
    suspend fun save(item: Manga) = withIoContext { mangaDao.insert(item.toEntity()) }
    suspend fun save(items: List<Manga>) = withIoContext { mangaDao.insert(items.toEntities()) }
    suspend fun delete(item: Manga) = withIoContext { mangaDao.delete(item.toEntity()) }
    suspend fun itemsByCategoryId(id: Long) =
        withIoContext { mangaDao.itemsByCategoryId(id).toModels() }

    suspend fun updateMangaBy(item: SiteCatalogElement) = withIoContext {
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
