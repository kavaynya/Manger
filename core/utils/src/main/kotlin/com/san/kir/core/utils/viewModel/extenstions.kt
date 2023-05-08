package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate

@Composable
inline fun <reified VM : StateHolder<*>> stateHolder(
    componentContext: ComponentContext = checkNotNull(LocalComponentContext.current) {
        "No ComponentContext was provided via LocalComponentContext"
    },
    crossinline creator: @DisallowComposableCalls () -> VM,
): VM {
    val key = VM::class.java.simpleName
    return remember(key) { componentContext.instanceKeeper.getOrCreate(key) { creator() } }
}

object LocalComponentContext {
    private val LocalComponentContext =
        compositionLocalOf<ComponentContext?> { null }

    val current: ComponentContext?
        @Composable
        get() = LocalComponentContext.current

    infix fun provides(componentContext: ComponentContext):
            ProvidedValue<ComponentContext?> {
        return LocalComponentContext.provides(componentContext)
    }
}


@Composable
fun StateHolder<*>.rememberSendEvent(event: ScreenEvent): () -> Unit {
    return remember { { sendEvent(event) } }
}
