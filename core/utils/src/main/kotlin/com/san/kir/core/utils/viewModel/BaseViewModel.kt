package com.san.kir.core.utils.viewModel

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.san.kir.core.utils.coroutines.defaultDispatcher
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
            .onEach { Timber.tag("ViewModel").i("NEW STATE $it") }
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
