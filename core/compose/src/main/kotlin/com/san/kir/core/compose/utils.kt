package com.san.kir.core.compose

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.san.kir.core.utils.navigation.rememberLambda

@Composable
public fun backPressed(): () -> Unit {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    return rememberLambda { dispatcher?.onBackPressed() }
}

@Composable
public fun isPortrait(): Boolean = LocalConfiguration.current.orientation != 2

@Composable
public fun isLandscape(): Boolean = LocalConfiguration.current.orientation == 2
