package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.FaultyDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.LocalComponentContext

@OptIn(FaultyDecomposeApi::class)
private val defaultAnimations: StackAnimation<NavConfig, NavContainer> =
    stackAnimation(false) { initial, target, direction ->
        if (direction.isFront) frontAnimation(initial.configuration)
        else frontAnimation(target.configuration)
    }

private fun frontAnimation(config: NavConfig) = ManualDI.navAnimation(config) ?: EmptyStackAnimator

@Composable
public fun NavHost(
    componentContext: ComponentContext,
    startConfig: NavConfig,
    stackAnimation: StackAnimation<NavConfig, NavContainer> = defaultAnimations,
) {
    val navHostComponent = remember {
        NavHostComponent(componentContext, startConfig, stackAnimation)
    }
    navHostComponent.Show()
}

@Composable
public fun NavHost(
    startConfig: NavConfig,
    stackAnimation: StackAnimation<NavConfig, NavContainer> = defaultAnimations,
): Unit = NavHost(LocalComponentContext.current, startConfig, stackAnimation)
