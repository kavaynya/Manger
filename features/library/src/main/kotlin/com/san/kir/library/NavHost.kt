package com.san.kir.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.accounts.AccountsNavHost
import com.san.kir.catalog.AddOnline
import com.san.kir.catalog.CatalogsNavHost
import com.san.kir.categories.CategoriesNavHost
import com.san.kir.chapters.Chapters
import com.san.kir.chapters.LatestCreator
import com.san.kir.chapters.ui.download.DownloadsScreen
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.circleShapeAnimator
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.support.MainMenuType
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.library.ui.library.LibraryNavigation
import com.san.kir.library.ui.library.LibraryScreen
import com.san.kir.library.ui.mangaAbout.MangaAboutScreen
import com.san.kir.schedule.ScheduleNavHost
import com.san.kir.settings.ui.settings.SettingsScreen
import com.san.kir.statistic.Statistic
import com.san.kir.statistic.StatisticNavHost
import com.san.kir.storage.Storage
import com.san.kir.storage.StorageNavHost
import kotlinx.parcelize.Parcelize
import timber.log.Timber

abstract class MenuNavConfig(val type: MainMenuType) : NavConfig

@Parcelize
internal class MainMenuItem : MenuNavConfig(MainMenuType.Library) {
    companion object {
        val creator = navCreator<MainMenuItem> {

            val navigation = remember {
                LibraryNavigation(
                    navigateToScreen = { type ->
                        if (MainMenuType.Library != type) {
                            Timber.i("navigate to $type")
                            mainMenuItems[type]?.let {
                                Timber.i("mainMenuItem $it")
                                simpleAdd(it).invoke()
                            }
                        }
                    },
                    navigateToInfo = simpleAdd(::About),
                    navigateToStorage = simpleAdd { id, params ->
                        Storage(id, params, true)
                    },
                    navigateToStats = simpleAdd(::Statistic),
                    navigateToChapters = simpleAdd(::Chapters),
                    navigateToOnline = simpleAdd(::Add),
                )
            }
            LibraryScreen(navigation)
        }
    }
}

@Parcelize
class StorageMenuItem(
    val mangaId: Long? = null,
    val params: SharedParams? = null,
    val hasUpdate: Boolean = false,
) : MenuNavConfig(MainMenuType.Storage) {
    companion object {
        val creator = navCreator<StorageMenuItem> {
            StorageNavHost(it.mangaId, it.params, it.hasUpdate)
        }
    }
}

@Parcelize
class CategoriesMenuItem : MenuNavConfig(MainMenuType.Category) {
    companion object {
        val creator = navCreator<CategoriesMenuItem> { CategoriesNavHost() }
    }
}

@Parcelize
class CatalogsMenuItem : MenuNavConfig(MainMenuType.Catalogs) {
    companion object {
        val creator = navCreator<CatalogsMenuItem> { CatalogsNavHost() }
    }
}

//  hasDeepLink = true
@Parcelize
class DownloadsMenuItem : MenuNavConfig(MainMenuType.Downloader) {
    companion object {
        val creator = navCreator<DownloadsMenuItem> { DownloadsScreen(backPressed()) }
    }
}

//  hasDeepLink = true
@Parcelize
class LatestMenuItem : MenuNavConfig(MainMenuType.Latest) {
    companion object {
        val creator = navCreator<LatestMenuItem> { LatestCreator() }
    }
}

@Parcelize
class SettingsMenuItem : MenuNavConfig(MainMenuType.Settings) {
    companion object {
        val creator = navCreator<SettingsMenuItem> { SettingsScreen(backPressed()) }
    }
}

@Parcelize
class StatisticsMenuItem :
    MenuNavConfig(MainMenuType.Statistic) {
    companion object {
        val creator = navCreator<StatisticsMenuItem> { StatisticNavHost() }
    }
}

@Parcelize
class ScheduleMenuItem : MenuNavConfig(MainMenuType.Schedule) {
    companion object {
        val creator = navCreator<ScheduleMenuItem> { ScheduleNavHost() }
    }
}

@Parcelize
class AccountsMenuItem : MenuNavConfig(MainMenuType.Accounts) {
    companion object {
        val creator = navCreator<AccountsMenuItem> { AccountsNavHost() }
    }
}

private val targets = listOf(
    MainMenuItem(),
    StorageMenuItem(),
    CategoriesMenuItem(),
    CatalogsMenuItem(),
    DownloadsMenuItem(),
    LatestMenuItem(),
    SettingsMenuItem(),
    StatisticsMenuItem(),
    ScheduleMenuItem(),
    AccountsMenuItem(),
)
val mainMenuItems = targets.associateBy { it.type }

@Parcelize
internal class About(val mangaId: Long) : NavConfig {
    companion object {
        val creator = navCreator<About> { config ->
            MangaAboutScreen(backPressed(), config.mangaId)
        }
    }
}

@Parcelize
internal class Add(val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Add> {
            CatalogsNavHost(AddOnline())
        }
    }
}

@Composable
fun LibraryNavHost() {
    NavHost(
        startConfig = MainMenuItem(),
        animation = animation
    ) { config ->
        when (config) {
            is MainMenuItem -> MainMenuItem.creator(config)
            is StorageMenuItem -> StorageMenuItem.creator(config)
            is CategoriesMenuItem -> CategoriesMenuItem.creator(config)
            is CatalogsMenuItem -> CatalogsMenuItem.creator(config)
            is DownloadsMenuItem -> DownloadsMenuItem.creator(config)
            is LatestMenuItem -> LatestMenuItem.creator(config)
            is SettingsMenuItem -> SettingsMenuItem.creator(config)
            is StatisticsMenuItem -> StatisticsMenuItem.creator(config)
            is ScheduleMenuItem -> ScheduleMenuItem.creator(config)
            is AccountsMenuItem -> AccountsMenuItem.creator(config)

            is Statistic -> Statistic.creator(config)
            is Chapters -> Chapters.creator(config)
            is Storage -> Storage.creator(config)
            is About -> About.creator(config)
            is Add -> Add.creator(config)
            else -> null
        }

    }
}

private val animation = stackAnimation<NavConfig, NavContainer> { initial, target, direction ->
    if (direction.isFront) frontAnimation(initial.configuration)
    else frontAnimation(target.configuration)
}

private fun frontAnimation(initial: NavConfig): StackAnimator {
    return when (initial) {
        is Add -> circleShapeAnimator(initial.params)
        is Storage -> itemShapeAnimator(initial.params, 0.1F)
        is Chapters -> itemShapeAnimator(initial.params)
        is Statistic -> itemShapeAnimator(initial.params, 0.1F)
        is MenuNavConfig -> horizontalSlide()
        else -> EmptyStackAnimator
    }
}
