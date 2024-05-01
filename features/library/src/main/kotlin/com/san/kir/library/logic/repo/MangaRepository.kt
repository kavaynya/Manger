package com.san.kir.library.logic.repo

import android.content.Context
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.CategoryDao
import com.san.kir.data.db.main.dao.MangaDao
import com.san.kir.data.db.main.entites.DbCategory
import com.san.kir.data.db.main.entites.DbManga
import com.san.kir.data.db.main.views.ViewManga
import com.san.kir.data.models.extend.CategoryWithMangas
import com.san.kir.data.models.utils.CATEGORY_ALL
import com.san.kir.data.models.utils.SortLibraryUtil
import com.san.kir.library.ui.library.ItemsState
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class MangaRepository(
    private val context: Context,
    private val mangaDao: MangaDao,
    private val categoryDao: CategoryDao,
) {
    //    Все категории
    private val _categories = categoryDao.loadItems()
    private val _mangas = mangaDao.loadSimpleItems().distinctUntilChanged()

    val itemsState = combine(_categories, _mangas) { cats, mangas ->
        if (cats.isEmpty())
            ItemsState.Empty
        else
            ItemsState.Ok(
                items = cats
                    .filter { it.isVisible }
                    .map { context.transform(it, mangas) },
                categories = cats.associate { it.id to it.name }.toPersistentMap()
            )
    }

    suspend fun item(mangaId: Long) = withIoContext { mangaDao.itemById(mangaId) }
    suspend fun categoryName(categoryId: Long) =
        withIoContext { categoryDao.itemById(categoryId).name }

    suspend fun update(manga: Manga) = withIoContext { mangaDao.update(manga) }

    suspend fun changeCategory(mangaId: Long, newCategoryId: Long) = withIoContext {
        mangaDao.update(mangaId, newCategoryId)
    }

    private fun Context.transform(
        cat: Category,
        mangas: List<SimplifiedManga>,
    ): CategoryWithMangas {
        var prepareMangas = mangas
            .filter { cat.name == CATEGORY_ALL || it.categoryId == cat.id }

        when (cat.typeSort) {
            com.san.kir.data.models.utils.SortLibraryUtil.add -> prepareMangas = prepareMangas.sortedBy { it.id }
            com.san.kir.data.models.utils.SortLibraryUtil.abc -> prepareMangas = prepareMangas.sortedBy { it.name }
            com.san.kir.data.models.utils.SortLibraryUtil.pop -> prepareMangas = prepareMangas.sortedBy { it.populate }
        }

        return CategoryWithMangas(
            id = cat.id,
            name = cat.name,
            typeSort = cat.typeSort,
            isReverseSort = cat.isReverseSort,
            spanPortrait = cat.spanPortrait,
            spanLandscape = cat.spanLandscape,
            isLargePortrait = cat.isLargePortrait,
            isLargeLandscape = cat.isLargeLandscape,
            mangas = if (cat.isReverseSort) prepareMangas.reversed() else prepareMangas
        )
    }
}
