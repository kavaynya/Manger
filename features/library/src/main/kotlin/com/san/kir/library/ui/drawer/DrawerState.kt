package com.san.kir.library.ui.drawer

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.entites.DbMainMenuItem


internal data class DrawerState(
    val hasEditMenu: Boolean = false,
    val menu: MainMenuItemsState = MainMenuItemsState.Load,
) : ScreenState

internal sealed interface MainMenuItemsState {
    data object Load : MainMenuItemsState
    data class Ok(val items: List<MenuItem>) : MainMenuItemsState
}

@Stable
internal data class MenuItem(
    val item: MainMenuItem,
    val status: String,
)
