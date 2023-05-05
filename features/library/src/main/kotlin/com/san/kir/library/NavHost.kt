package com.san.kir.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.san.kir.accounts.AccountsNavHost
import com.san.kir.catalog.AddOnline
import com.san.kir.catalog.CatalogsNavHost
import com.san.kir.categories.CategoriesNavHost
import com.san.kir.chapters.ui.chapters.ChaptersScreen
import com.san.kir.chapters.ui.download.DownloadsScreen
import com.san.kir.chapters.ui.latest.LatestScreen
import com.san.kir.core.compose.backPressed
import com.san.kir.core.support.MainMenuType
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.viewer.MangaViewer
import com.san.kir.library.ui.library.LibraryNavigation
import com.san.kir.library.ui.library.LibraryScreen
import com.san.kir.library.ui.mangaAbout.MangaAboutScreen
import com.san.kir.schedule.ScheduleNavHost
import com.san.kir.settings.ui.settings.SettingsScreen
import com.san.kir.statistic.StatisticNavHost
import com.san.kir.storage.StorageNavHost
import kotlinx.parcelize.Parcelize
import timber.log.Timber

private const val DURATION = 600

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
                    navigateToStorage = simpleAdd { StorageMenuItem(it, true) },
                    navigateToStats = simpleAdd(::StatisticsMenuItem),
                    navigateToChapters = simpleAdd(::Chapters),
                    navigateToOnline = simpleAdd(Add()),
                )
            }
            LibraryScreen(navigation)
        }
    }
}

@Parcelize
class StorageMenuItem(val mangaId: Long? = null, val hasUpdate: Boolean = false) :
    MenuNavConfig(MainMenuType.Storage) {
    companion object {
        val creator = navCreator<StorageMenuItem> {
            StorageNavHost(it.mangaId, it.hasUpdate)
        }
    }
}

@Parcelize
class CategoriesMenuItem : MenuNavConfig(MainMenuType.Category) {
    companion object {
        val creator = navCreator<CategoriesMenuItem> {
            CategoriesNavHost()
        }
    }
}

@Parcelize
class CatalogsMenuItem : MenuNavConfig(MainMenuType.Catalogs) {
    companion object {
        val creator = navCreator<CatalogsMenuItem> {
            CatalogsNavHost()
        }
    }
}

//  hasDeepLink = true
@Parcelize
class DownloadsMenuItem : MenuNavConfig(MainMenuType.Downloader) {
    companion object {
        val creator = navCreator<DownloadsMenuItem> {
            DownloadsScreen(backPressed())
        }
    }
}

//  hasDeepLink = true
@Parcelize
class LatestMenuItem : MenuNavConfig(MainMenuType.Latest) {
    companion object {
        val creator = navCreator<LatestMenuItem> {
            val context = LocalContext.current
            LatestScreen(
                navigateUp = backPressed(),
                navigateToViewer = remember { { MangaViewer.start(context, it) } },
            )
        }
    }
}

@Parcelize
class SettingsMenuItem : MenuNavConfig(MainMenuType.Settings) {
    companion object {
        val creator = navCreator<SettingsMenuItem> {
            SettingsScreen(backPressed())
        }
    }
}

@Parcelize
class StatisticsMenuItem(val statisticItemId: Long? = null) :
    MenuNavConfig(MainMenuType.Statistic) {
    companion object {
        val creator = navCreator<StatisticsMenuItem> {
            StatisticNavHost(it.statisticItemId)
        }
    }
}

@Parcelize
class ScheduleMenuItem : MenuNavConfig(MainMenuType.Schedule) {
    companion object {
        val creator = navCreator<ScheduleMenuItem> {
            ScheduleNavHost()
        }
    }
}

@Parcelize
class AccountsMenuItem : MenuNavConfig(MainMenuType.Accounts) {
    companion object {
        val creator = navCreator<AccountsMenuItem> {
            AccountsNavHost()
        }
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
internal class Chapters(val mangaId: Long) : NavConfig {
    companion object {
        val creator = navCreator<Chapters> { config ->
            val context = LocalContext.current
            ChaptersScreen(
                navigateUp = backPressed(),
                navigateToViewer = remember { { MangaViewer.start(context, it) } },
                mangaId = config.mangaId
            )
        }
    }
}

@Parcelize
internal class About(val mangaId: Long) : NavConfig {
    companion object {
        val creator = navCreator<About> { config ->
            MangaAboutScreen(backPressed(), config.mangaId)
        }
    }
}

@Parcelize
internal class Add : NavConfig {
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

            is Chapters -> Chapters.creator(config)
            is About -> About.creator(config)
            is Add -> Add.creator(config)
            else -> null
        }

    }
}

//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = targetState.destination.route
//    if (target == null) null
//    else
//        when {
//            GraphTree.Library.addOnline in target ->
//                scaleIn(
//                    animationSpec = tween(Constants.duration),
//                    initialScale = 0.08f,
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Library.item in target      ->
//                expandIn(
//                    animationSpec = tween(Constants.duration),
//                    expandFrom = Alignment.Center
//                )
//
//            GraphTree.Library.about in target     ->
//                slideInVertically(
//                    animationSpec = tween(Constants.duration),
//                    initialOffsetY = { it }
//                )
//
//            else                                  -> null
//        }
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val initial = initialState.destination.route
//
//    if (initial == null) null
//    else
//        when {
//            GraphTree.Library.addOnline in initial ->
//                scaleOut(
//                    animationSpec = tween(Constants.duration),
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Library.item in initial      ->
//                shrinkOut(
//                    animationSpec = tween(Constants.duration),
//                    shrinkTowards = Alignment.Center
//                )
//
//            GraphTree.Library.about in initial     ->
//                slideOutVertically(
//                    animationSpec = tween(Constants.duration),
//                    targetOffsetY = { it }
//                )
//
//            else                                   -> null
//        }
//}


//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition = {
//    //    Timber.tag("animation").d("main enter init   -> ${initialState.destination.route}")
//
//    val target = targetState.destination.route
//    val initial = initialState.destination.route
//    if (target != null && initial != null && GraphTree.Library.main in initial)
//        when {
//            GraphTree.Statistic.item in target ||
//                    GraphTree.Storage.item in target ->
//                slideInVertically(
//                    animationSpec = tween(Constants.duration),
//                    initialOffsetY = { it }
//                )
//
//            else ->
//                slideIntoContainer(
//                    AnimatedContentScope.SlideDirection.End,
//                    animationSpec = tween(Constants.duration)
//                )
//        }
//    else
//        slideIntoContainer(
//            AnimatedContentScope.SlideDirection.End,
//            animationSpec = tween(Constants.duration)
//        )
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition = {
//    //    Timber.tag("animation").d("main exit target  -> ${targetState.destination.route}")
//    //    Timber.tag("animation").d("main exit initial -> ${initialState.destination.route}")
//
//    val target = targetState.destination.route
//    val initial = initialState.destination.route
//
//    if (initial != null
//        && GraphTree.Library.main in initial
//        && target != null
//        && (GraphTree.Library.addOnline in target
//                || GraphTree.Library.item in target
//                || GraphTree.Library.about in target
//                || GraphTree.Statistic.item in target
//                || GraphTree.Storage.item in target))
//        fadeOut(animationSpec = tween(Constants.duration))
//    else
//        slideOutOfContainer(
//            AnimatedContentScope.SlideDirection.End, animationSpec = tween(Constants.duration)
//        )
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition = {
//    //    Timber.tag("animation").d("main popEnter init   -> ${initialState.destination.route}")
//    //    Timber.tag("animation").d("main popEnter target -> ${targetState.destination.route}")
//
//    val target = targetState.destination.route
//    val initial = initialState.destination.route
//
//    if (target != null
//        && GraphTree.Library.main in target
//        && initial != null
//        && (GraphTree.Library.addOnline in initial
//                || GraphTree.Library.item in initial
//                || GraphTree.Library.about in initial
//                || GraphTree.Statistic.item in initial
//                || GraphTree.Storage.item in initial))
//        fadeIn(animationSpec = tween(Constants.duration))
//    else
//        slideIntoContainer(
//            AnimatedContentScope.SlideDirection.Start, animationSpec = tween(Constants.duration)
//        )
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition = {
//    //    Timber.tag("animation").d("main popExit target -> ${targetState.destination.route}")
//
//    val initial = initialState.destination.route
//    val target = targetState.destination.route
//
//    if (initial != null && target != null && GraphTree.Library.main in target)
//        when {
//            GraphTree.Statistic.item in initial ||
//                    GraphTree.Storage.item in initial ->
//                slideOutVertically(
//                    animationSpec = tween(Constants.duration),
//                    targetOffsetY = { it }
//                )
//
//            else ->
//                slideOutOfContainer(
//                    AnimatedContentScope.SlideDirection.Start,
//                    animationSpec = tween(Constants.duration)
//                )
//        }
//    else
//        slideOutOfContainer(
//            AnimatedContentScope.SlideDirection.Start,
//            animationSpec = tween(Constants.duration)
//        )
//}
