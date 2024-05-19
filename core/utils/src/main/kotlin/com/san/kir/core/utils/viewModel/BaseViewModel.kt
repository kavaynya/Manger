package com.san.kir.core.utils.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.navigation.rememberLambda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber


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
fun StateHolder<*>.rememberSendAction(event: Action) = rememberLambda { sendAction(event) }

@Composable
fun StateHolder<*>.rememberSendAction() = rememberLambda(::sendAction)


abstract class ViewModel<S : ScreenState>(eventBus: EventBus = EventBusImpl()) :
    StateHolder<S>, EventBus by eventBus, CoroutineScope {

    protected abstract val tempState: Flow<S>
    protected abstract val defaultState: S

    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    override val state by lazy {
        tempState
            .onEach { Timber.tag("ViewModel").i("NEW STATE $it") }
            .flowOn(defaultDispatcher)
            .stateIn(this, SharingStarted.Lazily, defaultState)
    }

    protected abstract suspend fun onAction(action: Action)

    override fun sendAction(action: Action) {
        launch {
            Timber.tag("ViewModel").w("ON_ACTION: $action")
            when (action) {
                is ReturnEvents -> action.events.onEach { sendEvent(it) }
                else -> onAction(action)
            }
        }
    }

    override fun onDestroy() {
        cancel()
    }

    fun <T> Flow<T>.stateInSubscribed(defaultValue: T): StateFlow<T> {
        return stateIn(this@ViewModel, SharingStarted.WhileSubscribed(), defaultValue)
    }

    fun <T> Flow<T>.stateInEagerly(defaultValue: T): StateFlow<T> {
        return stateIn(this@ViewModel, SharingStarted.Eagerly, defaultValue)
    }
}
