package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.decompose.ComponentContext

public object LocalComponentContext {
    private val LocalComponentContext =
        staticCompositionLocalOf<ComponentContext> {
            error("No ComponentContext was provided via LocalComponentContext")
        }

    public val current: ComponentContext
        @Composable
        get() = LocalComponentContext.current

    public infix fun provides(componentContext: ComponentContext): ProvidedValue<ComponentContext> {
        return LocalComponentContext.provides(componentContext)
    }
}
