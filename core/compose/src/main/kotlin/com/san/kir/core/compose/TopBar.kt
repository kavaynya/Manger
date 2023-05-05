package com.san.kir.core.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.san.kir.core.compose.animation.FromStartToStartAnimContent
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.utils.TestTags
import kotlinx.coroutines.launch

@Composable
private fun PreparedTopBar(
    titleContent: @Composable ColumnScope.() -> Unit,
    subtitleContent: @Composable (() -> Unit)? = null,
    subtitle: String = "",
    height: Dp = Dimensions.appBarHeight,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable TopBarActions.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
) {

    val topBarActions = remember { TopBarActions() }
    val color by animateColorAsState(targetValue = backgroundColor)

    TopAppBar(
        title = {
            Column {
                titleContent()

                ProvideTextStyle(value = MaterialTheme.typography.subtitle2) {
                    if (subtitleContent != null) {
                        subtitleContent()
                    } else
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                maxLines = 1
                            )
                        }
                }
            }
        },
        navigationIcon = navigationIcon,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(height),
        actions = {
            Row(modifier = Modifier.endInsetsPadding()) {
                topBarActions.actions()
            }
        },
        backgroundColor = color,
    )
}

@Composable
fun topBar(
    title: String = "",
    titleContent: @Composable ColumnScope.() -> Unit = {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    subtitle: String = "",
    subtitleContent: @Composable (() -> Unit)? = null,
    actions: @Composable TopBarActions.() -> Unit = {},
    navigationButton: NavigationButton,
    initSearchText: String = "",
    onSearchTextChange: ((String) -> Unit)? = null,
    hasAction: Boolean = false,
    progressAction: Float? = null,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
): @Composable (Dp) -> Unit = {
    Column(modifier = Modifier.fillMaxWidth()) {
        PreparedTopBar(
            titleContent = titleContent,
            subtitleContent = subtitleContent,
            subtitle = subtitle,
            height = it,
            actions = actions,
            backgroundColor = backgroundColor,
            navigationIcon = { NavigationIcon(state = navigationButton) }
        )

        TopAnimatedVisibility(visible = onSearchTextChange != null) {
            SearchTextField(
                initialValue = initSearchText,
                onChangeValue = onSearchTextChange ?: {}
            )
        }

        TopAnimatedVisibility(visible = hasAction) {
            if (progressAction != null) {
                LinearProgressIndicator(
                    progress = progressAction,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun NavigationIcon(state: NavigationButton) {
    val coroutineScope = rememberCoroutineScope()

    FromStartToStartAnimContent(
        targetState = state,
        modifier = Modifier.startInsetsPadding()
    ) {
        when (it) {
            is NavigationButton.Back ->
                IconButton(
                    modifier = Modifier.testTag(TestTags.Drawer.nav_back),
                    onClick = { it.onClick() }
                ) { Icon(Icons.Default.ArrowBack, "") }

            is NavigationButton.Close ->
                IconButton(onClick = it.onClick) {
                    Icon(Icons.Default.Close, "")
                }

            is NavigationButton.Scaffold ->
                IconButton(
                    modifier = Modifier.testTag(TestTags.Drawer.drawer_open),
                    onClick = {
                        coroutineScope.launch { it.state.drawerState.open() }
                    }) { Icon(Icons.Default.Menu, "") }
        }
    }
}


class TopBarActions internal constructor() {

    @Composable
    fun MenuIcon(
        icon: ImageVector,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        onClick: () -> Unit,
    ) {
        IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
            Icon(icon, "", tint = tint)
        }
    }

    @Composable
    fun ExpandedMenu(
        actions: @Composable ExpandedMenuScope.() -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }

        MenuIcon(icon = Icons.Default.MoreVert) {
            expanded = true
        }

        ExpandedMenu(
            expanded = expanded,
            onCloseMenu = {
                expanded = false
            },
            actions = actions,
        )
    }
}

@Stable
sealed interface NavigationButton {
    @Stable
    data class Scaffold(val state: ScaffoldState) : NavigationButton

    @Stable
    data class Back(val onClick: () -> Unit) : NavigationButton

    @Stable
    data class Close(val onClick: () -> Unit) : NavigationButton
}
