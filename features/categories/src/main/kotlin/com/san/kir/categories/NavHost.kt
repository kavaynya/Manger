package com.san.kir.categories

import androidx.compose.runtime.Composable
import com.san.kir.categories.ui.categories.CategoriesScreen
import com.san.kir.categories.ui.category.CategoryScreen
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import kotlinx.parcelize.Parcelize

private const val DURATION = 600

@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            CategoriesScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Category)
            )
        }
    }
}

@Parcelize
internal class Category(val name: String) : NavConfig {
    companion object {
        val creator = navCreator<Category> { config ->
            CategoryScreen(
                navigateUp = backPressed(),
                categoryName = config.name
            )
        }
    }
}

@Composable
fun CategoriesNavHost() {
    NavHost(
        startConfig = Main(),
        animation = null,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Category -> Category.creator(config)
            else -> null
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Categories.main in target)
//        expandIn(animationSpec = tween(DURATION), expandFrom = Alignment.Center)
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Categories.item in target)
//        fadeOut(animationSpec = tween(DURATION))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Categories.item in target)
//        fadeIn(animationSpec = tween(DURATION))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Categories.main in target)
//        shrinkOut(animationSpec = tween(DURATION), shrinkTowards = Alignment.Center)
//    else null
//}
