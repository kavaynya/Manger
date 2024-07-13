package com.san.kir.catalog.ui.addOnline

import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.utils.viewModel.Event

internal sealed interface AddOnlineEvent : Event {
    data object ToUp : AddOnlineEvent
    data class ToNext(val url: String, val params: SharedParams): AddOnlineEvent
}
