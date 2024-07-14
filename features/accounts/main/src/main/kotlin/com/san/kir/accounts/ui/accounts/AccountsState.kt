package com.san.kir.accounts.ui.accounts

import android.accounts.Account
import com.san.kir.core.utils.viewModel.ScreenState


internal data class AccountsState(
    val currentAccounts: List<Account> = emptyList(),
    val availableAccounts: List<Account> = emptyList(),
) : ScreenState
