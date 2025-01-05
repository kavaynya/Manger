package com.san.kir.core.compose

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.san.kir.core.utils.navigation.BackHandler
import com.san.kir.core.utils.navigation.rememberDialogState

private val DropdownMenuVerticalPadding = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BaseScreen(
    drawerState: DrawerState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    menuActions: (@Composable ExpandedMenuScope.() -> Unit)? = null,
    topBar: @Composable (TopAppBarScrollBehavior?, MenuDialogState) -> Unit = { _, _ -> },
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val menuState: MenuDialogState = rememberDialogState(false)

    if (drawerContent == null) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalContainerColor provides containerColor,
        ) {
            ContentWrapper(
                scrollBehavior = scrollBehavior,
                contentColor = contentColor,
                containerColor = containerColor,
                topBar = { behavior -> topBar(behavior, menuState) },
                content = content
            )
        }
    } else {
        val drawerState = drawerState ?: rememberDrawerState(DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { ModalDrawerSheet { drawerContent.invoke(this) } },
        ) {
            ContentWrapper(
                scrollBehavior = scrollBehavior,
                contentColor = contentColor,
                containerColor = containerColor,
                topBar = { behavior -> topBar(behavior, menuState) },
                content = content
            )
        }

        BackHandler(drawerState.isOpen) {
            drawerState.close()
        }
    }

    if (menuActions != null) {
        TopEndSheets(
            dialogState = menuState,
            modifier = Modifier.topInsetsPadding(),
            animationSpec = spring(0.85f, 1000f)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = DropdownMenuVerticalPadding)
                    .width(IntrinsicSize.Max)
                    .verticalScroll(rememberScrollState())
                    .endInsetsPadding(),
            ) {
                val scope = remember { ExpandedMenuScope(menuState::dismiss) }
                scope.menuActions()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentWrapper(
    scrollBehavior: TopAppBarScrollBehavior?,
    containerColor: Color,
    contentColor: Color,
    topBar: @Composable (TopAppBarScrollBehavior?) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalContainerColor provides containerColor,
    ) {
        Box(
            modifier = Modifier
                .background(containerColor)
                .then(
                    if (scrollBehavior == null) Modifier
                    else Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                )
                .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                topBar.invoke(scrollBehavior)
                Box(modifier = Modifier.fillMaxSize()) {
                    content.invoke(this)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ScreenClear(
    drawerState: DrawerState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    topBar: @Composable (TopAppBarScrollBehavior?, MenuDialogState) -> Unit = { _, _ -> },
    menuActions: (@Composable ExpandedMenuScope.() -> Unit)? = null,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    BaseScreen(
        drawerState, scrollBehavior, containerColor, contentColor, menuActions, topBar,
        drawerContent, content
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ScreenContent(
    drawerState: DrawerState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    additionalPadding: Dp = Dimensions.zero,
    canScroll: Boolean = false,
    topBar: @Composable (TopAppBarScrollBehavior?, MenuDialogState) -> Unit = { _, _ -> },
    menuActions: (@Composable ExpandedMenuScope.() -> Unit)? = null,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val canScroll by remember { derivedStateOf { canScroll || scrollState.maxValue > 0 } }

    BaseScreen(
        drawerState = drawerState,
        scrollBehavior = scrollBehavior,
        menuActions = menuActions,
        topBar = topBar,
        drawerContent = drawerContent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = additionalPadding)
                .imePadding()
                .verticalScroll(scrollState, canScroll)
        ) {
            Spacer(modifier = Modifier.size(additionalPadding))

            content()

            if (WindowInsets.ime.getBottom(density) < 0) {
                Spacer(
                    modifier = Modifier
                        .height(additionalPadding)
                        .bottomInsetsPadding()
                )
            }

            if (additionalPadding > Dimensions.zero) {
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(
                        WindowInsets.navigationBars.add(WindowInsets(bottom = additionalPadding))
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ScreenList(
    additionalPadding: Dp = Dimensions.default,
    contentPadding: PaddingValues = bottomInsetsPadding(bottom = additionalPadding),
    drawerState: DrawerState? = null,
    state: LazyListState = rememberLazyListState(),
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    topBar: @Composable (TopAppBarScrollBehavior?, MenuDialogState) -> Unit = { _, _ -> },
    menuActions: (@Composable ExpandedMenuScope.() -> Unit)? = null,
    bottomContent: (@Composable BoxScope.() -> Unit) = {},
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
//    val canScroll by remember {
//        derivedStateOf {
//            val layoutInfo = state.layoutInfo
//            if (layoutInfo.totalItemsCount != 0) {
//                val visibleItemsInfo = layoutInfo.visibleItemsInfo
//                val firstVisibleItem = visibleItemsInfo.first()
//                val lastVisibleItem = visibleItemsInfo.last()
//                val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
//                Timber.d("Items -> ${layoutInfo.totalItemsCount}  \n" +
//                        "firstVisibleItem.index = ${firstVisibleItem.index}\n" +
//                        "firstVisibleItem.offset = ${firstVisibleItem.offset}\n" +
//                        "lastVisibleItem.index = ${lastVisibleItem.index}\n" +
//                        "lastVisibleItem.offset = ${lastVisibleItem.offset}\n" +
//                        "lastVisibleItem.size = ${lastVisibleItem.size}\n" +
//                        "viewportHeight = ${viewportHeight}")
//                if (firstVisibleItem.index != 0
//                    || firstVisibleItem.offset != 0
//                    || lastVisibleItem.index + 1 != layoutInfo.totalItemsCount
//                    || lastVisibleItem.offset + lastVisibleItem.size > viewportHeight
//                ) {
//                    Timber.d("Items -> ${layoutInfo.totalItemsCount}  == true")
//                    return@derivedStateOf true
//                }
//            }
//            Timber.d("Items -> ${layoutInfo.totalItemsCount}  == false")
//            false
//        }
//    }

    BaseScreen(
        drawerState = drawerState,
        scrollBehavior = scrollBehavior,
        menuActions = menuActions,
        topBar = topBar,
        drawerContent = drawerContent,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            state = state,
            contentPadding = contentPadding,
//            userScrollEnabled = canScroll,
            content = content
        )

        Box(modifier = Modifier.align(Alignment.BottomEnd), content = bottomContent)
    }
}
