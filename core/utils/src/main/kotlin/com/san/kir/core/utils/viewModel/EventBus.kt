package com.san.kir.core.utils.viewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber

public interface EventBus {

    public val events: SharedFlow<Event>
    public suspend fun sendEvent(event: Event)
}

public class EventBusImpl : EventBus {
    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    override suspend fun sendEvent(event: Event) {
        Timber.tag("EventBusImpl").i("EVENT($event)")
        events.emit(event)
    }
}
