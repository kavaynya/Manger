package com.san.kir.features.catalogs.allhen.ui.comx

import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.parsing.sites.Allhentai
import com.san.kir.features.catalogs.allhen.ui.allhen.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class ComxItemViewModel(
    private val manager: ConnectManager = ManualDI.connectManager,
) : ViewModel<ComxItemState>(), ComxItemStateHolder {
    private val loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    override val tempState = loginState.map { ComxItemState(it) }
    override val defaultState = ComxItemState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            ComxItemEvent.Update -> update()
        }
    }

    private suspend fun update() {
        loginState.value = LoginState.Loading
        runCatching {
            val document = manager.getDocument(Allhentai.HOST_NAME).select(".account-menu")
            val name = document.select("#accountMenu span.strong").first()?.text()
            val avatar = document.select(".user-profile-settings-link img").attr("src")

            if (name == null) loginState.value = LoginState.NonLogIn
            else loginState.value = LoginState.LogIn(name, avatar)
        }.onFailure {
            loginState.value = LoginState.Error
        }
    }
}




