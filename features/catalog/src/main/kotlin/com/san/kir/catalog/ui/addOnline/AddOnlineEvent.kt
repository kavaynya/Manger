package com.san.kir.catalog.ui.addOnline

import com.san.kir.core.utils.viewModel.Action

internal sealed interface AddOnlineEvent : Action {
    data class Update(val text: String) : AddOnlineEvent
}
