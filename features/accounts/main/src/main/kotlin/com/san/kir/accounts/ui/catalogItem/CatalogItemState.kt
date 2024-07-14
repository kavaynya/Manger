package com.san.kir.accounts.ui.catalogItem

import com.san.kir.core.utils.viewModel.ScreenState

internal data class CatalogItemState(
    val login: LoginState = LoginState.Loading,
) : ScreenState

internal sealed interface LoginState {
    data object Loading : LoginState
    data object NonLogIn : LoginState
    data class LogIn(val nickName: String, val avatar: String) : LoginState
    data object Error : LoginState
}
