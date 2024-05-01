package com.san.kir.library.ui.drawer

import com.san.kir.core.utils.viewModel.Action

internal sealed interface DrawerEvent : Action {
    data class Reorder(val from: Int, val to: Int) : DrawerEvent
}
