package com.san.kir.library.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.san.kir.core.compose.ExpandedMenuScope
import com.san.kir.core.compose.TopBarActions
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.library.R
import com.san.kir.library.ui.library.LibraryAction

internal fun libraryActions(navigateToOnline: (SharedParams) -> Unit): @Composable TopBarActions.() -> Unit {
    return {
        val buttonParams = rememberSharedParams()
        MenuIcon(
            icon = Icons.Default.Add,
            modifier = Modifier.saveParams(buttonParams),
            onClick = { navigateToOnline(buttonParams) }
        )
        ExpandedMenu()
    }
}

internal fun libraryMenu(sendAction: (LibraryAction) -> Unit): @Composable ExpandedMenuScope.() -> Unit {
    return {
        MenuText(
            R.string.update_current_category,
            onClick = { sendAction(LibraryAction.UpdateCurrentCategory) },
        )

        MenuText(
            R.string.update_all,
            onClick = { sendAction(LibraryAction.UpdateAll) },
        )

        MenuText(
            R.string.update_app,
            onClick = { sendAction(LibraryAction.UpdateApp) },
        )
    }
}
