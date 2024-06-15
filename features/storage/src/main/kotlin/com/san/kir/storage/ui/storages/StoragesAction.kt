package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.main.Storage

internal sealed interface StoragesAction : Action {
    data class Delete(val item: Storage) : StoragesAction
}
