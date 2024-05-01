package com.san.kir.storage.logic.repo

import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.dao.StorageDao
import com.san.kir.data.db.main.dao.itemByPath
import com.san.kir.data.db.main.entites.DbStorage

internal class StorageRepository(
    private val storageDao: StorageDao,
    private val mangaDao: MangaDao,
) {
    val items = storageDao.loadItems()
    val fullSize = storageDao.loadFullSize()

    suspend fun mangaFromPath(path: String) = mangaDao.itemByPath(getFullPath(path))
    fun storageFromFile(path: String) = storageDao.loadItemByPath(path)

    suspend fun delete(item: Storage) = withDefaultContext {
        kotlin.runCatching {
            storageDao.delete(item)
            getFullPath(item.path).deleteRecursively()
        }/*.onFailure {
            context.longToast(it.toString())
        }*/
    }

    fun loadManga(mangaId: Long) = mangaDao.loadItemById(mangaId)
}
