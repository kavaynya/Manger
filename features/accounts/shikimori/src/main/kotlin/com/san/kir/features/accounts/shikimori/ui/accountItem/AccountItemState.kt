package com.san.kir.features.accounts.shikimori.ui.accountItem

import com.san.kir.core.utils.viewModel.ScreenState

internal data class AccountItemState(
    val login: LoginState = LoginState.Loading,
) : ScreenState

internal sealed class LoginState(
    val nickName: String = "",
    val logo: String = ""
) {
    data object Loading : LoginState()
    class Ok(val accountId: Long, nickName: String, logo: String) : LoginState(nickName, logo)
    class LogInCheck(nickName: String, logo: String) : LoginState(nickName, logo)
    class LogInError(nickName: String) : LoginState(nickName)
    data object LogOut : LoginState()
    data object Error : LoginState()
}
