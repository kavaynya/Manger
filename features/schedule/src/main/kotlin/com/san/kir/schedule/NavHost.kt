package com.san.kir.schedule

import androidx.compose.runtime.Composable
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.schedule.ui.main.MainScreen
import com.san.kir.schedule.ui.task.TaskScreen
import kotlinx.parcelize.Parcelize

private const val DURATION = 600

@Parcelize
internal class MainConfig : NavConfig {
    companion object {
        val creator = navCreator<MainConfig> {
            MainScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::ScheduleConfig)
            )
        }
    }
}

@Parcelize
internal class ScheduleConfig(val id: Long) : NavConfig {
    companion object {
        val creator = navCreator<ScheduleConfig> { config ->
            TaskScreen(
                navigateUp = backPressed(),
                itemId = config.id
            )
        }
    }
}

@Composable
fun ScheduleNavHost() {
    NavHost(
        startConfig = MainConfig(),
        animation = null,
    ) { config ->
        when (config) {
            is MainConfig -> MainConfig.creator(config)
            is ScheduleConfig -> ScheduleConfig.creator(config)
            else -> null
        }
    }
}

//@OptIn(ExperimentalAnimationApi::class)
//private val enterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Schedule.main in target)
//        expandIn(animationSpec = tween(DURATION), expandFrom = Alignment.Center)
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val exitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Schedule.item in target)
//        fadeOut(animationSpec = tween(DURATION))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popEnterTransition: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
//    val target = initialState.destination.route
//    if (target != null && GraphTree.Schedule.item in target)
//        fadeIn(animationSpec = tween(DURATION))
//    else null
//}
//
//@OptIn(ExperimentalAnimationApi::class)
//private val popExitTransition: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
//    val target = targetState.destination.route
//    if (target != null && GraphTree.Schedule.main in target)
//        shrinkOut(animationSpec = tween(DURATION), shrinkTowards = Alignment.Center)
//    else null
//}
