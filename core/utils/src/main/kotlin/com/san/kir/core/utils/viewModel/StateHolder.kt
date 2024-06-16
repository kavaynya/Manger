package com.san.kir.core.utils.viewModel

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.flow.StateFlow

public interface StateHolder<S : ScreenState> : InstanceKeeper.Instance, EventBus {

    public val state: StateFlow<S>

    public fun sendAction(action: Action)

}
