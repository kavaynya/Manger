package com.san.kir.features.shikimori

import androidx.compose.runtime.Composable
import com.san.kir.catalog.CatalogsNavHost
import com.san.kir.catalog.GlobalSearch
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.shikimori.ui.accountRate.AccountRateScreen
import com.san.kir.features.shikimori.ui.accountScreen.AccountScreen
import com.san.kir.features.shikimori.ui.localItem.LocalItemScreen
import com.san.kir.features.shikimori.ui.localItems.LocalItemsScreen
import com.san.kir.features.shikimori.ui.search.ShikiSearchScreen
import kotlinx.parcelize.Parcelize

private const val duration = 600

@Parcelize
internal class MainConfig : NavConfig {
    companion object {
        val creator = navCreator<MainConfig> {
            AccountScreen(
                navigateUp = backPressed(),
                navigateToShikiItem = add(::ProfileItem),
                navigateToLocalItems = add(LocalItems()),
                navigateToSearch = add(Search())
            )
        }
    }
}

@Parcelize
internal class LocalItems : NavConfig {
    companion object {
        val creator = navCreator<LocalItems> {
            LocalItemsScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::LocalItem)
            )
        }
    }
}

@Parcelize
internal class LocalItem(val id: Long) : NavConfig {
    companion object {
        val creator = navCreator<LocalItem> { config ->
            LocalItemScreen(
                mangaId = config.id,
                navigateUp = backPressed(),
                navigateToSearch = add(::Search)
            )
        }
    }
}

@Parcelize
internal class Search(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<Search> { config ->
            ShikiSearchScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::ProfileItem),
                searchText = config.query
            )
        }
    }
}

@Parcelize
internal class ProfileItem(val mangaId: Long) : NavConfig {
    companion object {
        val creator = navCreator<ProfileItem> { config ->
            AccountRateScreen(
                navigateUp = backPressed(),
                navigateToSearch = add(::CatalogSearch),
                mangaId = config.mangaId,
            )
        }
    }
}

@Parcelize
internal class CatalogSearch(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<CatalogSearch> { config ->
            CatalogsNavHost(GlobalSearch(config.query))
        }
    }
}

@Composable
fun ShikimoriNavHost() {
    NavHost(startConfig = MainConfig()) {
        when (it) {
            is MainConfig -> MainConfig.creator(it)
            is LocalItems -> LocalItems.creator(it)
            is LocalItem -> LocalItem.creator(it)
            is Search -> Search.creator(it)
            is ProfileItem -> ProfileItem.creator(it)
            is CatalogSearch -> CatalogSearch.creator(it)
            else -> null
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val initial = initialState.destination.route
//    val target = targetState.destination.route
//
//    if (initial == null || target == null) null
//    else
//        when {
//            GraphTree.Accounts.Shikimori.search in target            ->
//                scaleIn(
//                    animationSpec = tween(duration),
//                    initialScale = 0.08f,
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Accounts.Shikimori.localItems in target        ->
//                scaleIn(
//                    animationSpec = tween(duration),
//                    initialScale = 0.08f,
//                    transformOrigin = TransformOrigin(0.9f, 0.90f)
//                )
//
//            GraphTree.Accounts.Shikimori.localItem in target ||
//                    GraphTree.Accounts.Shikimori.shikiItem in target ->
//                expandVertically(
//                    animationSpec = tween(duration),
//                    expandFrom = Alignment.CenterVertically
//                )
//
//            else                                                     -> null
//        }
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null) fadeOut(animationSpec = tween(duration))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null) fadeIn(animationSpec = tween(duration))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val initial = initialState.destination.route
//    val target = targetState.destination.route
//
//    if (initial == null || target == null) null
//    else
//        when {
//            GraphTree.Accounts.Shikimori.search in initial            ->
//                scaleOut(
//                    animationSpec = tween(duration),
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Accounts.Shikimori.localItems in initial        ->
//                scaleOut(
//                    animationSpec = tween(duration),
//                    transformOrigin = TransformOrigin(0.9f, 0.90f)
//                )
//
//            GraphTree.Accounts.Shikimori.localItem in initial ||
//                    GraphTree.Accounts.Shikimori.shikiItem in initial ->
//                shrinkVertically(
//                    animationSpec = tween(duration),
//                    shrinkTowards = Alignment.CenterVertically
//                )
//
//            else                                                      -> null
//        }
//}
