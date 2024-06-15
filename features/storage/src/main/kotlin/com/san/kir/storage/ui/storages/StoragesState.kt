package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.MangaLogo
import com.san.kir.data.models.main.Storage

internal data class StoragesState(
    val background: BackgroundState = BackgroundState(),
    val size: Double = 0.0,
    val count: Int = 0,
) : ScreenState

internal data class BackgroundState(val load: Boolean = true, val deleting: Int = 0) {
    val current = load || deleting > 0
}

internal data class StorageContainer(
    val storage: Storage,
    val mangaLogo: MangaLogo?,
)
