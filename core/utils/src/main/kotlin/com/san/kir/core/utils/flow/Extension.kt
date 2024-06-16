package com.san.kir.core.utils.flow

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.san.kir.core.utils.repeatOnLifecycle
import com.san.kir.core.utils.viewModel.LocalComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
public fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalComponentContext.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> = collectAsStateWithLifecycle(this.value, lifecycleOwner.lifecycle, minActiveState, context)

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
public fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsStateWithLifecycle(this.value, lifecycle, minActiveState, context)

@Composable
public fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T,
    lifecycleOwner: LifecycleOwner = LocalComponentContext.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsStateWithLifecycle(initial, lifecycleOwner.lifecycle, minActiveState, context)

@Composable
public fun <T> Flow<T>.collectAsStateWithLifecycle(
    initial: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = produceState(initial, this, lifecycle, minActiveState, context) {
    lifecycle.repeatOnLifecycle(minActiveState) {
        if (context == EmptyCoroutineContext) {
            collect { this@produceState.value = it }
        } else withContext(context) {
            collect { this@produceState.value = it }
        }
    }
}
