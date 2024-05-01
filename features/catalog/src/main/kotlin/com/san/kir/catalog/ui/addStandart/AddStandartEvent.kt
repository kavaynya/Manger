package com.san.kir.catalog.ui.addStandart

import com.san.kir.core.utils.viewModel.Action


internal sealed interface AddStandartEvent : Action {
    data class Set(val url: String) : AddStandartEvent
    data class UpdateText(val text: String) : AddStandartEvent
    data object StartProcess : AddStandartEvent
}
