package com.san.kir.catalog.ui.addStandart

import com.san.kir.core.utils.viewModel.Action


internal sealed interface AddStandartAction : Action {
    data class UpdateText(val text: String) : AddStandartAction
    data object StartProcess : AddStandartAction
}
