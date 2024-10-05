package com.san.kir.features.accounts.shikimori.ui.localItems

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.features.accounts.shikimori.logic.BackgroundTasks
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.useCases.BindStatus

@Stable
internal data class LocalItemsState(
    val action: BackgroundTasks = BackgroundTasks(),
    val unbind: List<BindStatus<LibraryMangaItem>> = emptyList(),
) : ScreenState {
    override fun toString(): String {
        return "LocalItemsState(action=$action, unBindSize=${unbind.count()})"
    }
}
