package com.san.kir.manger

import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.utils.navigation.NavConfig
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

            MaterialTheme(colorScheme = if (state.theme) darkColorScheme() else lightColorScheme()) {
                // Remember a SystemUiController
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colorScheme.isLight

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
        animation = stackAnimation(animator),
    ) { config ->
        when (config) {
            is Splash -> Splash.creator(config)
            is Library -> Library.creator(config)
            else -> null
        }
    }
}

private val animator = shapeAnimator(
    tween(durationMillis = 1200, easing = EaseInExpo)
) { factor ->
    GenericShape { size, _ ->
        val radius = size.height * factor
        addOval(Rect(Offset.Zero, radius))
        addOval(Rect(Offset(size.width, size.height), radius))
    }
}
