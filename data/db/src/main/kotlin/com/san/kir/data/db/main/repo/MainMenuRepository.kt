package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.MainMenuDao
import com.san.kir.data.db.main.entites.DbMainMenuItem
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.MainMenuItem
import com.san.kir.data.models.utils.MainMenuType
import kotlinx.coroutines.flow.Flow
import java.util.Collections

public class MainMenuRepository internal constructor(private val mainMenuDao: MainMenuDao) {
    public val items: Flow<List<MainMenuItem>> = mainMenuDao.loadItems().toModels()

    public suspend fun items(): List<MainMenuItem> =
        withIoContext { mainMenuDao.items().toModels() }

    public suspend fun swap(from: Int, to: Int): List<Long> = withIoContext {
        val items = mainMenuDao.items().toMutableList()
        Collections.swap(items, from, to)
        val insertItems = items.mapIndexed { index, item -> item.copy(order = index) }
        mainMenuDao.insert(insertItems)
    }

    public suspend fun insert(name: String, order: Int, type: MainMenuType): List<Long> =
        withIoContext {
            mainMenuDao.insert(DbMainMenuItem(name = name, order = order, type = type))
        }

    public suspend fun insert(items: List<MainMenuItem>): List<Long> =
        withIoContext { mainMenuDao.insert(items.toEntities()) }

    public suspend fun delete(item: MainMenuItem): Int =
        withIoContext { mainMenuDao.delete(item.toEntity()) }
}
