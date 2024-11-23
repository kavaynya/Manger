package com.san.kir.catalog.ui.search

import com.san.kir.core.utils.viewModel.ScreenState
import kotlinx.serialization.Serializable

internal data class SearchState(
    val background: Boolean = false,
    val catalog: List<SelectableCatalog> = emptyList(),
    val hasFilterChanges: Boolean = false,
    val addedMangaVisible: Boolean = false,
) : ScreenState {
    val selectedCatalogs = catalog.count { it.selected }
}

internal data class SelectableCatalog(
    val title: String,
    val name: String,
    val selected: Boolean,
)

@Serializable
internal data class FilterState(
    val selectedFilters: List<String> = emptyList(),
    val addedMangaVisible: Boolean = true,
) {
    fun hasChanges(other: FilterState): Boolean {
        if (addedMangaVisible == other.addedMangaVisible
            && selectedFilters.size == other.selectedFilters.size
        ) {
            return selectedFilters.toTypedArray()
                .contentEquals(other.selectedFilters.toTypedArray())
                .not()
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FilterState) return false
        return hasChanges(other).not()
    }

    override fun hashCode(): Int {
        var result = addedMangaVisible.hashCode()
        result = 31 * result + selectedFilters.hashCode()
        return result
    }
}
