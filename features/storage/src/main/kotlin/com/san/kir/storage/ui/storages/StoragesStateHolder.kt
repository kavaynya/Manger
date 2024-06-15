package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.StateHolder
import kotlinx.coroutines.flow.StateFlow

internal interface StoragesStateHolder : StateHolder<StoragesState> {
    val items: StateFlow<List<StorageContainer>>
}
