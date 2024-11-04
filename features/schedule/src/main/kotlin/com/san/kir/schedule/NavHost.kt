package com.san.kir.schedule

import NavEntry
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.schedule.ui.schedule.ScheduleScreen
import com.san.kir.schedule.ui.task.TaskScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

public val scheduleSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

@NavEntry
@Serializable
public data object Schedule : NavConfig() {
    internal val creator = navCreator<Schedule> {
        ScheduleScreen(
            navigateUp = backPressed(),
            navigateToItem = add(::Task)
        )
    }

    internal val animation = navAnimation<Schedule> { horizontalSlide() }
}

@NavEntry
@Serializable
public class Task(internal val id: Long, internal val params: SharedParams) : NavConfig() {
    internal companion object {
        val creator = navCreator<Task> { config ->
            TaskScreen(
                navigateUp = backPressed(),
                itemId = config.id
            )
        }

        internal val animation = navAnimation<Task> { shapeAnimator(it.params) }
    }
}
