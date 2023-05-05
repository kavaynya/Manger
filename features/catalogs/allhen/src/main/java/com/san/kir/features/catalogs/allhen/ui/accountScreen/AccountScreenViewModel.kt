package com.san.kir.features.catalogs.allhen.ui.accountScreen

import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.coroutines.flow.flowOf

internal class AccountScreenViewModel(

) : ViewModel<AccountScreenState>(), AccountScreenStateHolder {
    override val tempState = flowOf(AccountScreenState())

    override val defaultState = AccountScreenState()

    override suspend fun onEvent(event: ScreenEvent) {
        //        when(event) {
        //
        //        }
    }
}
