package com.san.kir.accounts.ui.accounts

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.coroutines.flow.flowOf

internal class AccountsViewModel(

) : ViewModel<AccountsState>(), AccountsStateHolder {
    override val tempState = flowOf(AccountsState())
    override val defaultState = AccountsState()

    override suspend fun onAction(action: Action) {}
}
