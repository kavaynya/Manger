package com.san.kir.storage.ui.storages

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.entites.DbStorage
import com.san.kir.data.db.main.custom.DbMinimalStorageManga

internal data class StoragesState(
    val items: List<DbStorage> = emptyList(),
    val mangas: List<DbMinimalStorageManga?> = emptyList(),
    val background: BackgroundState = BackgroundState.Load,
    val size: Double = items.sumOf { it.sizeFull },
    val count: Int = items.count(),
) : ScreenState

internal sealed interface BackgroundState {
    data object Load : BackgroundState
    data object None : BackgroundState
}
