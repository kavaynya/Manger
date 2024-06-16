package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

public object LocalEventBus {
    private val LocalEventBus =
        staticCompositionLocalOf<EventBus> { error("EventBus was not provided") }

    public val current: EventBus
        @Composable
        get() = LocalEventBus.current

    public infix fun provides(componentContext: EventBus): ProvidedValue<EventBus> {
        return LocalEventBus.provides(componentContext)
    }
}
