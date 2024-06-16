package com.san.kir.storage.ui.storage

import com.san.kir.core.utils.viewModel.Event

internal sealed interface StorageEvent : Event {
    data class ShowDeleteDialog(val mode: DeleteStatus) : StorageEvent
}
