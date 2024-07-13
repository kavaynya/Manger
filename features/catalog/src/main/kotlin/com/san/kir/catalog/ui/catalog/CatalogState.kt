package com.san.kir.catalog.ui.catalog

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.san.kir.catalog.R
import com.san.kir.core.utils.viewModel.ScreenState

internal class CatalogState : ScreenState

internal data class SelectableItem(
    val name: String,
    val state: Boolean
)

@Stable
internal data class BackgroundState(
    val updateItems: Boolean = false,
    val updateCatalogs: Boolean = false,
    val progress: Float? = null,
) {
    val currentState: Boolean
        get() = updateCatalogs || updateItems
}

@Stable
internal data class SortState(
    val type: SortType = SortType.Date,
    val reverse: Boolean = false,
    val hasPopulateSort: Boolean = true
)

internal sealed interface SortType {
    data object Date : SortType
    data object Name : SortType
    data object Pop : SortType
}

internal data class FilterState(
    val search: String = "",
    val selectedFilters: Map<FilterType, List<String>> = emptyMap()
) {
    val hasSelectedFilters = selectedFilters.isNotEmpty()
}

@Stable
internal data class Filter(
    val type: FilterType,
    val items: List<SelectableItem>
)

internal sealed class FilterType(@StringRes val stringId: Int) {
    data object Genres : FilterType(R.string.genres)
    data object Types : FilterType(R.string.manga_type)
    data object Statuses : FilterType(R.string.manga_status)
    data object Authors : FilterType(R.string.authors)
}
