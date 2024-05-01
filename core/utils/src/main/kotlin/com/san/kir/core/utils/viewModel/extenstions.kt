package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
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

@Composable
fun StateHolder<*>.rememberSendEvent(event: Action): () -> Unit {
    return remember { { sendAction(event) } }
}
