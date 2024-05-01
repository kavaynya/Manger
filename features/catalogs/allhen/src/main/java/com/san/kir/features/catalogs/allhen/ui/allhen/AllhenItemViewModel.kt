package com.san.kir.features.catalogs.allhen.ui.allhen

import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.parsing.sites.Allhentai
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class AllhenItemViewModel(
    private val manager: ConnectManager = ManualDI.connectManager,
) : ViewModel<AllhenItemState>(), AllhenItemStateHolder {
    private val loginState = MutableStateFlow<LoginState>(LoginState.Loading)

    override val tempState = loginState.map { AllhenItemState(it) }
    override val defaultState = AllhenItemState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            AllhenItemEvent.Update -> update()
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




