package com.san.kir.core.utils.viewModel

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.coroutines.defaultLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.Intrinsics

interface StateHolder<out S : ScreenState> : InstanceKeeper.Instance {
    val state: StateFlow<S>
    fun sendEvent(event: ScreenEvent)
}

abstract class ViewModel<out S : ScreenState> : StateHolder<S> {

    protected abstract val tempState: Flow<S>
    protected abstract val defaultState: S

    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override val state by lazy {
        tempState
            .onEach { Timber.tag("ViewModel").d("NEW STATE $it") }
            .flowOn(defaultDispatcher)
            .stateIn(viewModelScope, SharingStarted.Lazily, defaultState)
    }

    protected abstract suspend fun onEvent(event: ScreenEvent)

    override fun sendEvent(event: ScreenEvent) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            Timber.tag("ViewModel").w("ON_EVENT $event")
            onEvent(event)
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}

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
        return stateIn<T>(this@ViewModel, SharingStarted.Eagerly, defaultValue)
    }
}
