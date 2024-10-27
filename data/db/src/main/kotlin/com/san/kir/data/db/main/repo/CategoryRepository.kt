package com.san.kir.data.db.main.repo

import android.content.Context
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.categoryAll
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.CategoryDao
import com.san.kir.data.db.main.entites.DbCategory
import com.san.kir.data.db.main.mappers.toEntity
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Category
import com.san.kir.data.models.main.NameAndId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Collections

public class CategoryRepository internal constructor(
    private val context: Lazy<Context>,
    private val categoryDao: CategoryDao
) {
    public val count: Flow<Int> = categoryDao.loadItemsCount()
    public val items: Flow<List<Category>> = categoryDao.loadItems().toModels()
    public val names: Flow<List<String>> = categoryDao.loadNames()
    public val namesAndIds: Flow<List<NameAndId>> = categoryDao.loadNamesAndIds().toModels()
    public suspend fun names(): List<String> = withIoContext { categoryDao.names() }
    public fun categoryName(categoryId: Long): Flow<String> =
        categoryDao.loadItemById(categoryId).map { it.name }

    public suspend fun swap(from: Int, to: Int): List<Long> = withIoContext {
        val items = categoryDao.items().toMutableList()
        Collections.swap(items, from, to)
        categoryDao.insert(items.mapIndexed { i, m -> m.copy(order = i) })
    }

    public suspend fun insert(category: Category): List<Long> =
        withIoContext { categoryDao.insert(category.toEntity()) }

    public suspend fun insert(name: String): List<Long> =
        withIoContext { categoryDao.insert(DbCategory(name = name)) }

    public suspend fun item(categoryName: String): Category? =
        withIoContext { categoryDao.itemByName(categoryName)?.toModel() }

    public suspend fun item(id: Long): Category =
        withIoContext { categoryDao.itemById(id).toModel() }

    public suspend fun defaultCategory(): Category = item(ManualDI.categoryAll()) ?: Category()

    public suspend fun delete(item: Category): Int =
        withIoContext { categoryDao.delete(item.toEntity()) }

    public suspend fun idByName(name: String): Long? =
        withIoContext { categoryDao.itemByName(name)?.id }

    public suspend fun createNewCategory(): Category = Category(order = count.first() + 1)
}
