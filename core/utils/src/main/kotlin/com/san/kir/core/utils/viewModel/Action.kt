package com.san.kir.core.utils.viewModel

public interface Action

public data class ReturnEvents(val events: List<Event>) : Action

public fun ReturnEvents(vararg events: Event): ReturnEvents {
    return ReturnEvents(events.toList())
}

public fun Event.returned(): Action = ReturnEvents(this)
