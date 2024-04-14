package com.san.kir.core.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import com.san.kir.core.utils.viewModel.rememberLambda

@Composable
fun backPressed(): () -> Unit {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current
    return rememberLambda {
        dispatcher?.onBackPressedDispatcher?.onBackPressed()
    }
}
