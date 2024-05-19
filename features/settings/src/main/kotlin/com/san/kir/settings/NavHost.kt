package com.san.kir.settings

import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.settings.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object Settings : NavConfig() {
   internal val creator = navCreator<Settings> {
        SettingsScreen(backPressed())
    }

   internal val animation = navAnimation<Settings> { horizontalSlide() }
}
