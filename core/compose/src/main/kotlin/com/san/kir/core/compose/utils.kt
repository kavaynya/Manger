package com.san.kir.core.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.san.kir.core.utils.viewModel.rememberLambda

@Composable
fun backPressed(): () -> Unit {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    return remember { { dispatcher?.onBackPressed() } }
}

@Composable
fun backPressed(): () -> Unit {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    return rememberLambda { dispatcher?.onBackPressed() }
}
