package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable

public interface NavComponent<C : NavConfig> {
    @Composable
    public fun NavComponentScope.Render()

    public val config: C
}
