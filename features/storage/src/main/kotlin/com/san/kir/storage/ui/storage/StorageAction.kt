package com.san.kir.storage.ui.storage

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StorageAction : Action {
    data object DeleteAll : StorageAction
    data object DeleteRead : StorageAction
}
