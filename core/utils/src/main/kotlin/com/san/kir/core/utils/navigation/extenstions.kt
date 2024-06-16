package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator

@Composable
public fun rememberLambda(block: () -> Unit): () -> Unit = remember { block }

@Composable
public fun <P1> rememberLambda(block: (P1) -> Unit): (P1) -> Unit = remember { block }

@Composable
public fun <P1, P2> rememberLambda(block: (P1, P2) -> Unit): (P1, P2) -> Unit = remember { block }

@Composable
public fun <P1, P2, P3> rememberLambda(block: (P1, P2, P3) -> Unit): (P1, P2, P3) -> Unit =
    remember { block }

public fun <C : NavConfig> navCreator(
    block: @Composable NavComponentScope.(C) -> Unit,
): (C) -> NavComponent<C> = { navComponent(it, block) }

public fun <C : NavConfig> navAnimation(
    block: (C) -> StackAnimator
): (C) -> StackAnimator = { block(it) }

internal fun <C : NavConfig> navComponent(
    config: C,
    block: @Composable NavComponentScope.(C) -> Unit,
): NavComponent<C> = object : NavComponent<C> {
    override val config = config

    @Composable
    override fun NavComponentScope.Render() {
        block(config)
    }
}
