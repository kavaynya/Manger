package com.san.kir.catalog.ui.catalogs

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState



internal data class CatalogsState(
    val items: List<CheckableSite>,
    val background: Boolean,
) : ScreenState

@Stable
internal data class CheckableSite(
    val name: String,
    val host: String,
    val volume: VolumeState,
    val state: SiteState,
)

internal sealed interface VolumeState {
    data object Load : VolumeState
    data object Error : VolumeState
    data class Ok(val volume: Int, val diff: Int) : VolumeState
}

internal sealed interface SiteState {
    data object Load : SiteState
    data object Error : SiteState
    data object Ok : SiteState
}
