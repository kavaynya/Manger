package com.san.kir.manger.ui.init

import androidx.compose.runtime.Composable
import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.core.utils.viewModel.rememberSendAction

internal interface InitStateHolder : StateHolder<InitState>

@Composable
internal fun InitStateHolder.next(onSuccess: () -> Unit) = rememberSendAction(InitEvent.Next(onSuccess))
