package com.san.kir.statistic

import NavEntry
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.statistic.ui.statistic.StatisticScreen
import com.san.kir.statistic.ui.statistics.StatisticsScreen
import kotlinx.serialization.Serializable


fun statisticsNavigationCreators() {
    AddNavigationCreators
}

@NavEntry
@Serializable
object Statistics : NavConfig() {
    internal val creator = navCreator<Statistics> {
        StatisticsScreen(
            navigateUp = backPressed(),
            navigateToItem = add { id, params -> Statistic(itemId = id, params = params) },
        )
    }

    internal val animation = navAnimation<Statistics> { horizontalSlide() }
}

@NavEntry
@Serializable
class Statistic(val itemId: Long = -1, val mangaId: Long = -1, val params: SharedParams) :
    NavConfig() {
    companion object {
        val creator = navCreator<Statistic> { config ->
            StatisticScreen(
                navigateUp = backPressed(),
                itemId = config.itemId,
                mangaId = config.mangaId,
            )
        }

        val animation = navAnimation<Statistic> { itemShapeAnimator(it.params, 0.1f) }
    }
}
