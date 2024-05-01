package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberLambda(block: () -> Unit) = remember { block }

@Composable
fun <P1> rememberLambda(block: (P1) -> Unit) = remember { block }

@Composable
fun <P1, P2> rememberLambda(block: (P1, P2) -> Unit) = remember { block }

@Composable
fun <P1, P2, P3> rememberLambda(block: (P1, P2, P3) -> Unit) = remember { block }

fun <C : NavConfig> navCreator(
    block: @Composable NavComponentScope.(C) -> Unit,
): (C) -> NavComponent<C> = { navComponent(it, block) }

fun <C : NavConfig> navComponent(
    config: C,
    block: @Composable NavComponentScope.(C) -> Unit,
): NavComponent<C> = object : NavComponent<C> {
    override val config = config

    @Composable
    override fun NavComponentScope.Render() {
        block(config)
    }
}
