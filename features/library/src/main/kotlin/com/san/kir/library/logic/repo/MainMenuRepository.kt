package com.san.kir.library.logic.repo

import android.app.Application
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.data.db.dao.CategoryDao
import com.san.kir.data.db.dao.ChapterDao
import com.san.kir.data.db.dao.MainMenuDao
import com.san.kir.data.db.dao.MangaDao
import com.san.kir.data.db.dao.PlannedDao
import com.san.kir.data.db.dao.StorageDao
import com.san.kir.data.models.base.MainMenuItem
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.library.R
import com.san.kir.library.ui.drawer.MenuItem
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Collections
import javax.inject.Inject

internal class MainMenuRepository @Inject constructor(
    private val context: Application,
    private val mainMenuDao: MainMenuDao,
    mangaDao: MangaDao,
    storageDao: StorageDao,
    categoryDao: CategoryDao,
    manager: SiteCatalogsManager,
    chapterDao: ChapterDao,
    plannedDao: PlannedDao,
) {

    val items =
        combine(
            mainMenuDao.loadItems(),
            combine(
                mangaDao.loadItemsCount(),
                storageDao.loadFullSize().map { it.toInt() },
                categoryDao.loadItemsCount(),
                chapterDao.loadDownloadCount(),
                chapterDao.loadLatestCount(),
                plannedDao.loadItemsCount()
            ) { Transition(it) }
        ) { items, transition ->
            items.map(transform(manager.catalog.size, transition))
        }.distinctUntilChanged()

    suspend fun swap(from: Int, to: Int) {
        val items = mainMenuDao.getItems().toMutableList()
        Collections.swap(items, from, to)
        mainMenuDao.update(*items.mapIndexed { i, m -> m.copy(order = i) }.toTypedArray())
    }

    private fun transform(siteCount: Int, transition: Transition): (MainMenuItem) -> MenuItem = {
        when (it.type) {
            com.san.kir.data.models.utils.MainMenuType.Default,
            com.san.kir.data.models.utils.MainMenuType.Library -> MenuItem(it, transition.libraryCount)

            com.san.kir.data.models.utils.MainMenuType.Category -> MenuItem(it, transition.categoryCount)
            com.san.kir.data.models.utils.MainMenuType.Catalogs -> MenuItem(it, "$siteCount")
            com.san.kir.data.models.utils.MainMenuType.Downloader -> MenuItem(it, transition.downloadCount)
            com.san.kir.data.models.utils.MainMenuType.Latest -> MenuItem(it, transition.latestCount)
            com.san.kir.data.models.utils.MainMenuType.Schedule -> MenuItem(it, transition.plannedCount)
            com.san.kir.data.models.utils.MainMenuType.Settings,
            com.san.kir.data.models.utils.MainMenuType.Statistic,
            com.san.kir.data.models.utils.MainMenuType.Accounts -> MenuItem(it, "")

            com.san.kir.data.models.utils.MainMenuType.Storage -> {
                MenuItem(
                    it,
                    context.getString(R.string.main_menu_storage_size_mb, transition.storageSize)
                )
            }
        }
    }

    private class Transition(content: Array<Int>) {
        val libraryCount: String = content[0].toString()
        val storageSize: String = content[1].toString()
        val categoryCount: String = content[2].toString()
        val downloadCount: String = content[3].toString()
        val latestCount: String = content[4].toString()
        val plannedCount: String = content[5].toString()
    }
}
