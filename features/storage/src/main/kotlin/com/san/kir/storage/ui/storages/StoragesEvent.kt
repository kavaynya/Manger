package com.san.kir.storage.ui.storages

import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.utils.viewModel.Event

internal sealed interface StoragesEvent : Event {
    data class ToStorage(
        val id: Long,
        val params: SharedParams,
        val hasUpdate: Boolean,
    ) : StoragesEvent
}
