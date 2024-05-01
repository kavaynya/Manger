package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.MainMenuDao
import com.san.kir.data.db.main.entites.DbMainMenuItem
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.MainMenuItem
import com.san.kir.data.models.utils.MainMenuType
import java.util.Collections

class MainMenuRepository internal constructor(private val mainMenuDao: MainMenuDao) {
    val items = mainMenuDao.loadItems().toModels()

    suspend fun items() = withIoContext { mainMenuDao.items().toModels() }

    suspend fun swap(from: Int, to: Int) = withIoContext {
        val items = mainMenuDao.items().toMutableList()
        Collections.swap(items, from, to)
        val insertItems = items.mapIndexed { index, item -> item.copy(order = index) }
        mainMenuDao.insert(insertItems)
    }

    suspend fun insert(name: String, order: Int, type: MainMenuType) = withIoContext {
        mainMenuDao.insert(DbMainMenuItem(name = name, order = order, type = type))
    }

    suspend fun insert(items: List<MainMenuItem>) =
        withIoContext { mainMenuDao.insert(items.toEntities()) }

    suspend fun delete(item: MainMenuItem) =
        withIoContext { mainMenuDao.delete(item.toEntity()) }
}
