package com.san.kir.statistic

import NavEntry
import androidx.compose.runtime.remember
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.statistic.ui.statistic.StatisticScreen
import com.san.kir.statistic.ui.statistics.StatisticsScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule


public val statisticsSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

@NavEntry
@Serializable
public data object Statistics : NavConfig() {
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
public data class Statistic(
    internal val itemId: Long = -1,
    internal val mangaId: Long = -1,
    internal val params: SharedParams
) : NavConfig() {
    internal companion object {
        internal val creator = navCreator<Statistic> { config ->
            StatisticScreen(
                navigateUp = backPressed(),
                itemId = remember { config.itemId },
                mangaId = remember { config.mangaId },
            )
        }

        internal val animation = navAnimation<Statistic> { itemShapeAnimator(it.params, 0.1f) }
    }
}
