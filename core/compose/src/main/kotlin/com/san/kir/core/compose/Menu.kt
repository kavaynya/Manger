package com.san.kir.core.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

class ExpandedMenuScope internal constructor(
    private val onCloseMenu: () -> Unit,
) {

    @Composable
    fun MenuText(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        DropdownMenuItem(
            onClick = {
                onClick()
                onCloseMenu()
            },
            modifier = modifier,
            text = { Text(text) }
        )
    }

    @Composable
    fun MenuText(id: Int, modifier: Modifier = Modifier, onClick: () -> Unit) =
        MenuText(stringResource(id), modifier, onClick)

    @Composable
    fun CheckedMenuText(
        id: Int,
        checked: Boolean,
        onClick: () -> Unit,
    ) {
        DropdownMenuItem(
            onClick = onClick,
            text = { Text(stringResource(id)) },
            trailingIcon = {
                Checkbox(
                    checked,
                    {
                        onClick()
                        onCloseMenu()
                    },
                    modifier = Modifier.padding(end = Dimensions.quarter)
                )
            },
        )
    }
}

@Composable
fun ExpandedMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onCloseMenu: () -> Unit,
    actions: @Composable ExpandedMenuScope.() -> Unit,
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onCloseMenu, modifier = modifier) {
        ExpandedMenuScope(onCloseMenu).actions()
    }
}
