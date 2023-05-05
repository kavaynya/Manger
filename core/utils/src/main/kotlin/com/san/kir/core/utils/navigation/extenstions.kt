package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberLambda(block: () -> Unit) = remember { block }

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
