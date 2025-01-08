package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StoragesAction : Action {
    data class Delete(val id: Long) : StoragesAction
}
