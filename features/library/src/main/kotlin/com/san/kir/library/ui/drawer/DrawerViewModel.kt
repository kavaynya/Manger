package com.san.kir.library.ui.drawer

import com.san.kir.background.works.UpdateMainMenuWorker
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.categoryRepository
import com.san.kir.data.chapterRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.db.main.repo.ChapterRepository
import com.san.kir.data.db.main.repo.MainMenuRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.PlannedRepository
import com.san.kir.data.db.main.repo.StorageRepository
import com.san.kir.data.mainMenuRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.MainMenuItem
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.plannedRepository
import com.san.kir.data.storageRepository
import com.san.kir.library.R
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class DrawerViewModel(
    private val mainMenuRepository: MainMenuRepository = ManualDI.mainMenuRepository(),
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
    mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
    chapterRepository: ChapterRepository = ManualDI.chapterRepository(),
    storageRepository: StorageRepository = ManualDI.storageRepository(),
    plannedRepository: PlannedRepository = ManualDI.plannedRepository(),
) : ViewModel<DrawerState>(), DrawerStateHolder {

    override val tempState = combine(
        mainMenuRepository.items,
        combine(
            mangaRepository.count,
            storageRepository.fullSizeInt,
            categoryRepository.count,
            chapterRepository.downloadCount,
            chapterRepository.latestCount,
            plannedRepository.count
        ) { Transition(it) }
    ) { menuItems, counts ->
        val preTransform = transform(manager.catalog.size, counts)
        menuItems.map { preTransform(it) }
    }
        .distinctUntilChanged()
        .onStart { UpdateMainMenuWorker.addTask() }
        .map { DrawerState(MainMenuItemsState.Ok(it)) }

    override val defaultState = DrawerState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is DrawerAction.Reorder -> mainMenuRepository.swap(action.from, action.to)
        }
    }

    private fun transform(siteCount: Int, transition: Transition): (MainMenuItem) -> MenuItem =
        { item ->
            when (item.type) {
                MainMenuType.Default, MainMenuType.Library ->
                    MenuItem(item, transition.libraryCount)

                MainMenuType.Category ->
                    MenuItem(item, transition.categoryCount)

                MainMenuType.Catalogs ->
                    MenuItem(item, siteCount.toString())

                MainMenuType.Downloader ->
                    MenuItem(item, transition.downloadCount)

                MainMenuType.Latest ->
                    MenuItem(item, transition.latestCount)

                MainMenuType.Schedule ->
                    MenuItem(item, transition.plannedCount)

                MainMenuType.Settings, MainMenuType.Statistic, MainMenuType.Accounts ->
                    MenuItem(item, "")

                MainMenuType.Storage ->
                    MenuItem(
                        item,
                        ManualDI.application.getString(R.string.size_format, transition.storageSize)
                    )
            }
        }

    private class Transition(content: Array<Int>) {
        val libraryCount = content[0].toString()
        val storageSize = content[1].toString()
        val categoryCount = content[2].toString()
        val downloadCount = content[3].toString()
        val latestCount = content[4].toString()
        val plannedCount = content[5].toString()
    }
}
