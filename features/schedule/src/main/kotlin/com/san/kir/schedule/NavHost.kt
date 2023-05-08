package com.san.kir.schedule

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.schedule.ui.main.MainScreen
import com.san.kir.schedule.ui.task.TaskScreen
import kotlinx.parcelize.Parcelize

@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            MainScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::Schedule)
            )
        }
    }
}

@Parcelize
internal class Schedule(val id: Long, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Schedule> { config ->
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
        startConfig = Main(),
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Schedule -> Schedule.creator(config)
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
        is Schedule -> shapeAnimator(initial.params)
        else -> EmptyStackAnimator
    }
}
