package com.san.kir.storage

import androidx.compose.runtime.Composable
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.storage.ui.storage.StorageScreen
import com.san.kir.storage.ui.storages.StoragesScreen
import kotlinx.parcelize.Parcelize

private const val DURATION = 600

@Parcelize
private class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            StoragesScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Storage)
            )
        }
    }
}

@Parcelize
private class Storage(val mangaId: Long, val hasUpdate: Boolean = false) : NavConfig {
    companion object {
        val creator = navCreator<Storage> { config ->
            StorageScreen(
                navigateUp = backPressed(),
                mangaId = config.mangaId,
                hasUpdate = config.hasUpdate
            )
        }
    }
}

@Composable
fun StorageNavHost(mangaId: Long? = null, hasUpdate: Boolean = false) {
    NavHost(
        startConfig = mangaId?.let { Storage(it, hasUpdate) } ?: Main(),
        animation = null,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Storage -> Storage.creator(config)
            else -> null
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Storage.main in target)
//        expandIn(animationSpec = tween(Constants.duration), expandFrom = Alignment.Center)
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Storage.item in target)
//        fadeOut(animationSpec = tween(Constants.duration))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Storage.item in target)
//        fadeIn(animationSpec = tween(Constants.duration))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Storage.main in target)
//        shrinkOut(animationSpec = tween(Constants.duration), shrinkTowards = Alignment.Center)
//    else null
//}
