package com.san.kir.statistic

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
import com.san.kir.statistic.ui.statistic.StatisticScreen
import com.san.kir.statistic.ui.statistics.StatisticsScreen
import kotlinx.parcelize.Parcelize

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
class Statistic(val itemId: Long, val params: SharedParams) : NavConfig {
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
fun StatisticNavHost() {
    NavHost(
        startConfig = Main(),
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Statistic -> Statistic.creator(config)
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
        is Statistic -> shapeAnimator(initial.params)
        else -> EmptyStackAnimator
    }
}
