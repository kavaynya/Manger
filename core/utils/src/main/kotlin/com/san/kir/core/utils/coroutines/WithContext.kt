package com.san.kir.core.utils.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

public suspend inline fun <T> withMainContext(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(mainDispatcher, block)

public suspend inline fun <T> withIoContext(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(ioDispatcher, block)

public suspend inline fun <T> withDefaultContext(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(defaultDispatcher, block)
