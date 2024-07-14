package com.san.kir.features.shikimori.ui.localItems

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.views.ViewMangaWithChapterCounts
import com.san.kir.features.shikimori.logic.BackgroundTasks
import com.san.kir.features.shikimori.logic.useCases.BindStatus

@Stable
internal data class LocalItemsState(
    val action: BackgroundTasks = BackgroundTasks(),
    val unbind: List<BindStatus<ViewMangaWithChapterCounts>> = emptyList(),
) : ScreenState {
    override fun toString(): String {
        return "LocalItemsState(action=$action, unBindSize=${unbind.count()})"
    }
}
