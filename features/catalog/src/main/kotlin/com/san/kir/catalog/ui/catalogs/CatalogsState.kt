package com.san.kir.catalog.ui.catalogs

import com.san.kir.core.utils.viewModel.ScreenState


internal data class CatalogsState(
    val items: List<CheckableSite> = emptyList(),
    val background: Boolean = true,
) : ScreenState

internal data class CheckableSite(
    val name: String,
    val host: String,
    val volume: VolumeState,
    val state: SiteState,
)

internal sealed interface VolumeState {
    data object Load : VolumeState
    data object Error : VolumeState
    data class Ok(val volume: Int, val diff: Int, val isPositive: Boolean) : VolumeState
}

internal enum class SiteState { Load, Error, Ok }
