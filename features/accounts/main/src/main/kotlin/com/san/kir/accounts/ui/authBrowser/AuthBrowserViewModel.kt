package com.san.kir.accounts.ui.authBrowser

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.coroutines.flow.flowOf

internal class AuthBrowserViewModel : ViewModel<AuthBrowserState>(), AuthBrowserStateHolder {
    override val tempState = flowOf(AuthBrowserState())
    override val defaultState = AuthBrowserState()

    override suspend fun onAction(action: Action) {}
}
