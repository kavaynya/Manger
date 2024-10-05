package com.san.kir.features.accounts.shikimori.ui.accountScreen

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.BackgroundTasks
import com.san.kir.features.accounts.shikimori.ui.accountItem.LoginState

@Stable
internal data class AccountState(
    val login: LoginState = LoginState.Loading,
    val action: BackgroundTasks = BackgroundTasks(),
) : ScreenState
