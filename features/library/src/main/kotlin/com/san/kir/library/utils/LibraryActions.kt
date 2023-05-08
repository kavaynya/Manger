package com.san.kir.library.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.san.kir.core.compose.TopBarActions
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.library.R
import com.san.kir.library.ui.library.LibraryEvent

internal fun libraryActions(
    navigateToOnline: (SharedParams) -> Unit,
    sendEvent: (LibraryEvent) -> Unit,
): @Composable TopBarActions.() -> Unit = {

    val buttonParams = rememberSharedParams()
    MenuIcon(
        icon = Icons.Default.Add,
        modifier = Modifier.onGloballyPositioned { coordinates ->
            buttonParams.bounds = coordinates.boundsInWindow()
        }
    ) {
        navigateToOnline(buttonParams)
    }

    ExpandedMenu {
        MenuText(id = R.string.library_menu_reload) { sendEvent(LibraryEvent.UpdateCurrentCategory) }
        MenuText(id = R.string.library_menu_reload_all) { sendEvent(LibraryEvent.UpdateAll) }
        MenuText(id = R.string.library_menu_update) { sendEvent(LibraryEvent.UpdateApp) }
    }
}
