package com.san.kir.background.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.san.kir.core.utils.ManualDI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun collectWorkInfoByTag(tag: String, action: suspend (List<WorkInfo>) -> Unit) {
    WorkManager.getInstance(ManualDI.context)
        .getWorkInfosByTagLiveData(tag)
        .asFlow()
        .collect(action)
}

/**
 * Creates a [Flow] containing values dispatched by originating [LiveData]: at the start
 * a flow collector receives the latest value held by LiveData and then observes LiveData updates.
 *
 * When a collection of the returned flow starts the originating [LiveData] becomes
 * [active][LiveData.onActive]. Similarly, when a collection completes [LiveData] becomes
 * [inactive][LiveData.onInactive].
 *
 * BackPressure: the returned flow is conflated. There is no mechanism to suspend an emission by
 * LiveData due to a slow collector, so collector always gets the most recent value emitted.
 */
fun <T> LiveData<T>.asFlow(): Flow<T> = callbackFlow {
    val observer = Observer(::trySend)

    withContext(Dispatchers.Main.immediate) {
        observeForever(observer)
    }

    awaitClose {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            removeObserver(observer)
        }
    }
}.conflate()
