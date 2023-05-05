package com.san.kir.catalog

import androidx.compose.runtime.Composable
import com.san.kir.catalog.ui.addOnline.AddOnlineScreen
import com.san.kir.catalog.ui.addStandart.AddStandartScreen
import com.san.kir.catalog.ui.catalog.CatalogScreen
import com.san.kir.catalog.ui.catalogItem.CatalogItemScreen
import com.san.kir.catalog.ui.catalogs.CatalogsScreen
import com.san.kir.catalog.ui.search.SearchScreen
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import kotlinx.parcelize.Parcelize

private const val DURATION = 600

// hasDeepLink = true
@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> { config ->
            CatalogsScreen(
                navigateUp = backPressed(),
                navigateToSearch = add(GlobalSearch()),
                navigateToItem = add(::Catalog)
            )
        }
    }
}

@Parcelize
internal class Catalog(val catalogName: String) : NavConfig {
    companion object {
        val creator = navCreator<Catalog> { config ->
            CatalogScreen(
                navigateUp = back(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                catalogName = config.catalogName
            )
        }
    }
}

@Parcelize
internal class Info(val url: String) : NavConfig {
    companion object {
        val creator = navCreator<Info> { config ->
            CatalogItemScreen(
                navigateUp = backPressed(),
                navigateToAdd = add(::AddLocal),
                url = config.url
            )
        }
    }
}

@Parcelize
internal class AddLocal(val url: String) : NavConfig {
    companion object {
        val creator = navCreator<AddLocal> { config ->
            AddStandartScreen(
                navigateUp = backPressed(),
                url = config.url
            )
        }
    }
}

@Parcelize
class GlobalSearch(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<GlobalSearch> { config ->
            SearchScreen(
                navigateUp = backPressed(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                searchText = config.query
            )
        }
    }
}

@Parcelize
class AddOnline : NavConfig {
    companion object {
        val creator = navCreator<AddOnline> { config ->
            AddOnlineScreen(
                navigateUp = backPressed(),
                navigateToNext = add(::AddLocal),
            )
        }
    }
}

@Composable
fun CatalogsNavHost(startConfig: NavConfig = Main()) {
    NavHost(
        startConfig = startConfig,
        animation = null,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Catalog -> Catalog.creator(config)
            is GlobalSearch -> GlobalSearch.creator(config)
            is Info -> Info.creator(config)
            is AddLocal -> AddLocal.creator(config)
            is AddOnline -> AddOnline.creator(config)
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
//            GraphTree.Catalogs.search in target   ->
//                scaleIn(
//                    animationSpec = tween(DURATION),
//                    initialScale = 0.08f,
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Catalogs.item in target     ->
//                expandVertically(
//                    animationSpec = tween(DURATION),
//                    expandFrom = Alignment.CenterVertically
//                )
//
//            GraphTree.Catalogs.itemInfo in target ->
//                slideInVertically(
//                    animationSpec = tween(DURATION),
//                    initialOffsetY = { it }
//                )
//
//            GraphTree.Catalogs.itemAdd in target  ->
//                scaleIn(
//                    animationSpec = tween(DURATION),
//                    initialScale = 0.1f,
//                    transformOrigin = TransformOrigin(0.95f, 0.01f)
//                )
//
//            else                                  -> null
//        }
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null) fadeOut(animationSpec = tween(DURATION))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null) fadeIn(animationSpec = tween(DURATION))
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
//            GraphTree.Catalogs.search in initial   ->
//                scaleOut(
//                    animationSpec = tween(DURATION),
//                    transformOrigin = TransformOrigin(0.9f, 0.05f)
//                )
//
//            GraphTree.Catalogs.item in initial     ->
//                shrinkVertically(
//                    animationSpec = tween(DURATION),
//                    shrinkTowards = Alignment.CenterVertically
//                )
//
//            GraphTree.Catalogs.itemInfo in initial ->
//                slideOutVertically(
//                    animationSpec = tween(DURATION),
//                    targetOffsetY = { it }
//                )
//
//            GraphTree.Catalogs.itemAdd in initial  ->
//                scaleOut(
//                    animationSpec = tween(DURATION),
//                    transformOrigin = TransformOrigin(0.95f, 0.01f)
//                )
//
//            else                                   -> null
//        }
//}
