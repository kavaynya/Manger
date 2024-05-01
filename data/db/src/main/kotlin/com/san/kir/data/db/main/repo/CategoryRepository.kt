package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.CategoryDao
import com.san.kir.data.db.main.entites.DbCategory
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Category
import com.san.kir.data.models.utils.CATEGORY_ALL
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Collections

class CategoryRepository internal constructor(private val categoryDao: CategoryDao) {
    val count = categoryDao.loadItemsCount()
    val items = categoryDao.loadItems().toModels()
    val names = categoryDao.loadNames()
    val namesAndIds = categoryDao.loadNamesAndIds().toModels()
    suspend fun names() = withIoContext { categoryDao.names() }
    fun categoryName(categoryId: Long) = categoryDao.loadItemById(categoryId).map { it.name }

    suspend fun swap(from: Int, to: Int) = withIoContext {
        val items = categoryDao.items().toMutableList()
        Collections.swap(items, from, to)
        categoryDao.insert(items.mapIndexed { i, m -> m.copy(order = i) })
    }

    suspend fun insert(category: Category) =
        withIoContext { categoryDao.insert(category.toEntity()) }

    suspend fun insert(name: String) =
        withIoContext { categoryDao.insert(DbCategory(name = name)) }

    suspend fun item(categoryName: String) =
        withIoContext { categoryDao.itemByName(categoryName).toModel() }

    suspend fun item(id: Long) =
        withIoContext { categoryDao.itemById(id).toModel() }

    suspend fun defaultCategory() = item(ManualDI.application.CATEGORY_ALL)

    suspend fun delete(item: Category) =
        withIoContext { categoryDao.delete(item.toEntity()) }

    suspend fun idByName(name: String) =
        withIoContext { categoryDao.itemByName(name).id }

    suspend fun createNewCategory() = Category(order = count.first() + 1)
}
