package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.navigation.rememberLambda

@Composable
inline fun <reified VM : StateHolder<*>> stateHolder(
    key: Any = VM::class.java.simpleName,
    componentContext: ComponentContext = checkNotNull(LocalComponentContext.current) {
        "No ComponentContext was provided via LocalComponentContext"
    },
    crossinline creator: @DisallowComposableCalls (EventBus) -> VM,
): VM {
    val eventBus = LocalEventBus.current
    return remember(key) { componentContext.instanceKeeper.getOrCreate(key) { creator(eventBus) } }
}

@Composable
inline fun <reified VM : StateHolder<*>> stateHolder(
    key: Any = VM::class.java.simpleName,
    componentContext: ComponentContext = checkNotNull(LocalComponentContext.current) {
        "No ComponentContext was provided via LocalComponentContext"
    },
): VM = remember(key) { componentContext.instanceKeeper.get(key) as VM }

@Composable
fun StateHolder<*>.rememberSendAction(event: Action) = rememberLambda { sendAction(event) }

@Composable
fun StateHolder<*>.rememberSendAction() = rememberLambda(::sendAction)

@Composable
fun rememberSendEvent(): (Event) -> Unit {
    val eventBus = LocalEventBus.current
    val scope = rememberCoroutineScope()

   return rememberLambda { event ->
        scope.defaultLaunch {
            eventBus.sendEvent(event)
        }
    }
}

@Composable
fun EventBus.OnEvent(handle: (Event) -> Unit) {
    LaunchedEffect(Unit) {
        events.collect(handle)
    }
}

@Composable
fun OnGlobalEvent(handle: (Event) -> Unit) {
    LocalEventBus.current.OnEvent(handle)
}
