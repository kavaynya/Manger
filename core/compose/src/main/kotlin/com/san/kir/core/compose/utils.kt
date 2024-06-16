package com.san.kir.core.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import com.san.kir.core.utils.navigation.rememberLambda

@Composable
public fun backPressed(): () -> Unit {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    return rememberLambda { dispatcher?.onBackPressed() }
}
