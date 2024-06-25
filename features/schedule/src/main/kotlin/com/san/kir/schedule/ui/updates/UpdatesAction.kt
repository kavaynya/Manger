package com.san.kir.schedule.ui.updates

import com.san.kir.core.utils.viewModel.Action


internal sealed interface UpdatesAction : Action {
    data class Update(val itemId: Long, val updateState: Boolean) : UpdatesAction
}
