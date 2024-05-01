package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.db.main.entites.DbStorage

internal sealed interface StoragesEvent : Action {
    data class Delete(val item: Storage) : StoragesEvent
}
