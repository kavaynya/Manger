package com.san.kir.library.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.startInsetsPadding
import com.san.kir.core.utils.TestTags
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.library.R

// Боковое меню с выбором пунктов для навигации по приложению
@Composable
internal fun DrawerScreen(navigateToScreen: (MainMenuType) -> Unit) {

    val holder: DrawerStateHolder = stateHolder { DrawerViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()

    var editMode by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: ""
    }

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .padding(Dimensions.half)
                .startInsetsPadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.mipmap.ic_launcher_foreground), "")

            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.app_name_format, version))
                Text(stringResource(R.string.author_name))
            }

            OutlinedIconToggleButton(
                checked = editMode,
                onCheckedChange = { editMode = it },
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                if (editMode) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit menu")
                } else {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit menu")
                }
            }
        }

        when (val current = state.menu) {
            MainMenuItemsState.Load ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            is MainMenuItemsState.Ok ->
                // Навигация по пунктам приложения
                Column {
                    current.items.forEachIndexed { index, item ->
                        MainMenuItemRows(
                            index = index,
                            max = current.items.size,
                            item = item,
                            editMode = editMode,
                            sendAction = holder::sendAction,
                            action = { navigateToScreen(item.item.type) }
                        )
                    }
                }
        }
    }
}

// Шаблон пункта меню
@Composable
private fun MainMenuItemRows(
    index: Int,
    max: Int,
    item: MenuItem,
    editMode: Boolean,
    sendAction: (DrawerAction) -> Unit,
    action: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.Drawer.item)
    ) {

        NavigationDrawerItem(
            label = { Text(item.item.name) },
            selected = false,
            onClick = action,
            icon = { Icon(item.item.type.icon, "") },
            badge = { Text(item.status) }
        )

        StartAnimatedVisibility(
            editMode,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Dimensions.half),
        ) {
            Row(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50)
                )
            ) {
                IconButton(
                    onClick = { sendAction(DrawerAction.Reorder(index, index - 1)) },
                    enabled = index > 0
                ) {
                    Icon(Icons.Default.ArrowDropUp, "")
                }

                IconButton(
                    onClick = { sendAction(DrawerAction.Reorder(index, index + 1)) },
                    enabled = index < max - 1
                ) {
                    Icon(Icons.Default.ArrowDropDown, "")
                }
            }
        }
    }
}
