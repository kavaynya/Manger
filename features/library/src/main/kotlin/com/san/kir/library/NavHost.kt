package com.san.kir.library

import NavEntry
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.san.kir.accounts.Accounts
import com.san.kir.accounts.accountsNavigationCreators
import com.san.kir.catalog.AddOnline
import com.san.kir.catalog.Catalogs
import com.san.kir.catalog.catalogsNavigationCreators
import com.san.kir.categories.Categories
import com.san.kir.categories.categoryNavigationCreators
import com.san.kir.chapters.Chapters
import com.san.kir.chapters.Downloads
import com.san.kir.chapters.Latest
import com.san.kir.chapters.chaptersNavigationCreators
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.EmptyStackAnimator
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.library.ui.library.LibraryNavigation
import com.san.kir.library.ui.library.LibraryScreen
import com.san.kir.library.ui.mangaAbout.MangaAboutScreen
import com.san.kir.schedule.Schedule
import com.san.kir.schedule.scheduleNavigationCreators
import com.san.kir.settings.Settings
import com.san.kir.settings.settingsNavigationCreators
import com.san.kir.statistic.Statistic
import com.san.kir.statistic.Statistics
import com.san.kir.statistic.statisticsNavigationCreators
import com.san.kir.storage.Storage
import com.san.kir.storage.Storages
import com.san.kir.storage.storageNavigationCreators
import kotlinx.serialization.Serializable
import timber.log.Timber

private fun libraryNavigationCreators() {
    AddNavigationCreators
    storageNavigationCreators()
    categoryNavigationCreators()
    catalogsNavigationCreators()
    scheduleNavigationCreators()
    statisticsNavigationCreators()
    accountsNavigationCreators()
    chaptersNavigationCreators()
    settingsNavigationCreators()
}

private val mainMenuItems = mapOf(
    MainMenuType.Library to Library,
    MainMenuType.Storage to Storages,
    MainMenuType.Category to Categories,
    MainMenuType.Catalogs to Catalogs,
    MainMenuType.Downloader to Downloads,
    MainMenuType.Latest to Latest,
    MainMenuType.Settings to Settings,
    MainMenuType.Schedule to Schedule,
    MainMenuType.Statistic to Statistics,
    MainMenuType.Accounts to Accounts,
)


@NavEntry
@Serializable
internal data object Library : NavConfig() {
    val creator = navCreator<Library> {
        val navigation = remember {
            LibraryNavigation(
                toScreen = { type ->
                    if (MainMenuType.Library != type) {
                        Timber.i("navigate to $type")
                        mainMenuItems[type]?.let {
                            Timber.i("mainMenuItem $it")
                            simpleAdd(it).invoke()
                        }
                    }
                },
                toInfo = simpleAdd(::About),
                toStorage = simpleAdd { id, params -> Storage(id, params) },
                toStats = simpleAdd { mangaId, params ->
                    Statistic(mangaId = mangaId, params = params)
                },
                toChapters = simpleAdd(::Chapters),
                toOnline = simpleAdd(::AddOnline),
            )
        }
        LibraryScreen(navigation)
    }

    val animation = navAnimation<Library> { EmptyStackAnimator }
}

@NavEntry
@Serializable
internal class About(val mangaId: Long, val sharedParams: SharedParams) : NavConfig() {
    companion object {
        val creator = navCreator<About> { config ->
            MangaAboutScreen(backPressed(), config.mangaId)
        }

        val animation = navAnimation<About> { config ->
            itemShapeAnimator(config.sharedParams, 0.1f)
        }
    }
}

@Composable
public fun LibraryNavHost() {
    libraryNavigationCreators()
    NavHost(startConfig = Library)
}

