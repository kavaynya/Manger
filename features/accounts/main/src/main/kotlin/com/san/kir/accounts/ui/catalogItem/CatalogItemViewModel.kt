package com.san.kir.accounts.ui.catalogItem

import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.EventBus
import com.san.kir.core.utils.viewModel.UpdateEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.parsing.SiteConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CatalogItemViewModel(
    private val eventBus: EventBus,
    private val siteConstants: SiteConstants,
    private val manager: ConnectManager = ManualDI.connectManager(),
) : ViewModel<CatalogItemState>(), CatalogItemStateHolder {
    private val loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    override val tempState = loginState.map(::CatalogItemState)
    override val defaultState = CatalogItemState()

    init {
        eventBus
            .events
            .filterIsInstance<UpdateEvent>()
            .onEach { update() }
            .launchIn(this)
        sendAction(CatalogItemAction.Update)
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            CatalogItemAction.Update -> update()
        }
    }

    private suspend fun update() {
        loginState.value = LoginState.Loading

        runCatching {
            val user = siteConstants.User(manager)

            if (user == null) loginState.value = LoginState.NonLogIn
            else loginState.value = LoginState.LogIn(user.login, user.avatar)
        }.onFailure {
            loginState.value = LoginState.Error
        }
    }
}




