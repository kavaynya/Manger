package com.san.kir.manger.ui.init

import androidx.compose.runtime.Composable
import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.core.utils.viewModel.rememberSendAction

interface InitStateHolder : StateHolder<InitState>

@Composable
inline fun InitStateHolder.next(crossinline onSuccess: () -> Unit): () -> Unit {
    return rememberSendAction(InitEvent.Next { onSuccess() })
}
