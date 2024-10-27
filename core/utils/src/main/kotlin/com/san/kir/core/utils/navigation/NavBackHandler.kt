package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.essenty.backhandler.BackHandler

public interface NavBackHandler : BackHandler {
    public fun backPress()

    @Composable
    public fun backPressed(): () -> Unit {
        return rememberLambda { backPress() }
    }
}
