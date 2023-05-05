package com.san.kir.statistic

import androidx.compose.runtime.Composable
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.statistic.ui.statistic.StatisticScreen
import com.san.kir.statistic.ui.statistics.StatisticsScreen
import kotlinx.parcelize.Parcelize

private const val DURATION = 600

@Parcelize
private class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            StatisticsScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Statistic),
            )
        }
    }
}

@Parcelize
private class Statistic(val itemId: Long) : NavConfig {
    companion object {
        val creator = navCreator<Statistic> { config ->
            StatisticScreen(
                navigateUp = backPressed(),
                itemId = config.itemId,
            )
        }
    }
}

@Composable
fun StatisticNavHost(statisticItemId: Long? = null) {
    NavHost(
        startConfig = statisticItemId?.let(::Statistic) ?: Main(),
        animation = null,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Statistic -> Statistic.creator(config)
            else -> null
        }
    }
}

/*@OptIn(ExperimentalAnimationApi::class)
private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    val target = initialState.destination.route
    if (target != null && GraphTree.Statistic.main in target)
        expandIn(animationSpec = tween(Constants.duration), expandFrom = Alignment.Center)
    else null
}

@OptIn(ExperimentalAnimationApi::class)
private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    val target = targetState.destination.route
    if (target != null && GraphTree.Statistic.item in target)
        fadeOut(animationSpec = tween(Constants.duration))
    else null
}

@OptIn(ExperimentalAnimationApi::class)
private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    val target = initialState.destination.route
    if (target != null && GraphTree.Statistic.item in target)
        fadeIn(animationSpec = tween(Constants.duration))
    else null
}

@OptIn(ExperimentalAnimationApi::class)
private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    val target = targetState.destination.route
    if (target != null && GraphTree.Statistic.main in target)
        shrinkOut(animationSpec = tween(Constants.duration), shrinkTowards = Alignment.Center)
    else null
}*/
