package com.san.kir.manger

import androidx.compose.animation.core.tween
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.library.LibraryNavHost
import com.san.kir.manger.ui.init.InitScreen
import kotlinx.parcelize.Parcelize

@Parcelize
private class Splash : NavConfig {
    companion object {
        val creator = navCreator<Splash> {
            InitScreen(replace(Library()))
        }
    }
}

@Parcelize
class Library : NavConfig {
    companion object {
        val creator = navCreator<Library> {
            val mainViewModel: MainStateHolder = stateHolder { MainViewModel() }
            val state by mainViewModel.state.collectAsState()

            MaterialTheme(colors = if (state.theme) darkColors() else lightColors()) {
                // Remember a SystemUiController
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight

                SideEffect {
                    // Update all of the system bar colors to be transparent, and use
                    // dark icons if we're in light theme
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                LibraryNavHost()
            }
        }
    }
}

@Composable
fun MainNavHost(componentContext: ComponentContext) {
    NavHost(
        componentContext = componentContext,
        startConfig = Splash(),
        animation = animation,
    ) { config ->
        when (config) {
            is Splash -> Splash.creator(config)
            is Library -> Library.creator(config)
            else -> null
        }
    }
}

private val animation: StackAnimation<NavConfig, NavContainer> =
    stackAnimation(
        slide(
            tween(
                durationMillis = 700
            )
        )
    )
