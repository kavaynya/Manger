package com.san.kir.core.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.lerp
import com.san.kir.core.utils.TestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
private fun indicator(pagerState: PagerState): @Composable (tabPositions: List<TabPosition>) -> Unit =
    { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTabs(
    pagerState: PagerState,
    items: ImmutableList<String>,
    modifier: Modifier = Modifier,
    onTabClick: suspend (index: Int) -> Unit = { pagerState.animateScrollToPage(it) },
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val tabClicker: (Int) -> Unit = remember { { scope.launch { onTabClick(it) } } }

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = indicator(pagerState),
        divider = {},
        modifier = modifier.background(MaterialTheme.colors.primarySurface)
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                modifier = Modifier.testTag(TestTags.Library.tab),
                selected = pagerState.currentPage == index,
                text = { Text(text = item) },
                onClick = { tabClicker(index) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    pagerState: PagerState,
    items: ImmutableList<Int>,
    onTabClick: suspend (index: Int) -> Unit = { pagerState.animateScrollToPage(it) },
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val tabClicker: (Int) -> Unit = remember { { scope.launch { onTabClick(it) } } }

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = indicator(pagerState),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                selected = pagerState.currentPage == index,
                text = { Text(text = stringResource(id = item)) },
                onClick = { tabClicker(index) }
            )
        }
    }
}

/**
 * This indicator syncs up a [TabRow] or [ScrollableTabRow] tab indicator with a
 * [androidx.compose.foundation.pager.HorizontalPager] or
 * [androidx.compose.foundation.pager.VerticalPager].
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier {
    val stateBridge = object : PagerStateBridge {
        override val currentPage: Int
            get() = pagerState.currentPage
        override val currentPageOffset: Float
            get() = pagerState.currentPageOffsetFraction
    }

    return pagerTabIndicatorOffset(stateBridge, tabPositions, pageIndexMapping)
}

private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerStateBridge,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}

internal interface PagerStateBridge {
    val currentPage: Int
    val currentPageOffset: Float
}
