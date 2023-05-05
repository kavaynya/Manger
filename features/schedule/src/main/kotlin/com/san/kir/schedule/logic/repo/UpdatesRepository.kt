package com.san.kir.schedule.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.dao.MangaDao

class UpdatesRepository(
    private val mangaDao: MangaDao,
) {
    val items = mangaDao.loadMiniItems()

    suspend fun update(itemId: Long, update: Boolean) =
        withIoContext { mangaDao.update(itemId, update) }
}
