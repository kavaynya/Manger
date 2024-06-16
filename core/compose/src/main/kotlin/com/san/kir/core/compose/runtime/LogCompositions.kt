package com.san.kir.core.compose.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.san.kir.core.compose.BuildConfig
import timber.log.Timber

public class Ref(public var value: Int)

@Composable
public inline fun LogCompositions(msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Timber.tag("Compose").d("Compositions: $msg ${ref.value}")
    }
}
