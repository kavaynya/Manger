package com.san.kir.settings

import NavEntry
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.settings.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

public fun settingsNavigationCreators() {
    AddNavigationCreators
}

@NavEntry
@Serializable
public object Settings : NavConfig() {
   internal val creator = navCreator<Settings> {
        SettingsScreen(backPressed())
    }

   internal val animation = navAnimation<Settings> { horizontalSlide() }
}
