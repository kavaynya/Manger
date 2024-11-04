package com.san.kir.catalog.ui.addStandart

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState

@Stable
internal data class AddStandartState(
    val categoryName: String = "",
    val availableCategories: List<String> = emptyList(),
    val progress: Int = 0,
    val processState: ProcessState = ProcessState.None,
) : ScreenState {
    val hasAllow = categoryName.length >= 3
    val createNewCategory =
        hasAllow && availableCategories.size != 1 || availableCategories.firstOrNull() != categoryName
}

internal sealed interface ProcessState {
    data object Load : ProcessState
    data object Error : ProcessState
    data object Complete : ProcessState
    data object None : ProcessState
}

internal object ProcessStatus {
    const val CATEGORY_CHANGED = 1
    const val PREV_AND_UPDATE_MANGA = 2
    const val PREV_AND_CREATED_FOLDER = 3
    const val PREV_AND_SEARCH_CHAPTERS = 4
    const val ALL_COMPLETE = 5
}
