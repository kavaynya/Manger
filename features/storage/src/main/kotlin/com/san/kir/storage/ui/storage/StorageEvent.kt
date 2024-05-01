package com.san.kir.storage.ui.storage

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StorageEvent : Action {
    data class Set(val mangaId: Long, val hasUpdate: Boolean) : StorageEvent
    data object DeleteAll : StorageEvent
    data object DeleteRead : StorageEvent
}
