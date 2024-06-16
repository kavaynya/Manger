package com.san.kir.core.utils.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun CoroutineScope.mainLaunch(block: suspend CoroutineScope.() -> Unit): Job =
    launch(mainDispatcher, block = block)

public fun CoroutineScope.defaultLaunch(block: suspend CoroutineScope.() -> Unit): Job =
    launch(defaultDispatcher, block = block)

public fun CoroutineScope.ioLaunch(block: suspend CoroutineScope.() -> Unit): Job =
    launch(ioDispatcher, block = block)

public fun CoroutineScope.defaultExcLaunch(
    onFailure: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(
    defaultDispatcher + CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        onFailure()
    },
    block = block
)
