package com.san.kir.core.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.san.kir.core.utils.TestTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val TabItemShape = RoundedCornerShape(70)

@OptIn(ExperimentalFoundationApi::class)
private fun indicator(pagerState: PagerState): @Composable (List<TabPosition>) -> Unit =
    { tabPositions ->
        val tab = tabPositions.getOrNull(pagerState.currentPage) ?: tabPositions.first()

        Box(
            modifier = Modifier
                .tabIndicatorOffset(tab)
                .fillMaxSize()
                .padding(Dimensions.quarter)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), TabItemShape)
        )
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTabs(
    pagerState: PagerState,
    items: List<String>,
    modifier: Modifier = Modifier,
    onTabClick: suspend (index: Int) -> Unit = { pagerState.animateScrollToPage(it) },
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val tabClicker: (Int) -> Unit = remember { { scope.launch { onTabClick(it) } } }

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = indicator(pagerState),
        divider = {},
        modifier = modifier.horizontalInsetsPadding(),
        edgePadding = Dimensions.default,
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                modifier = Modifier
                    .testTag(TestTags.Library.tab)
                    .clip(TabItemShape),
                selected = pagerState.currentPage == index,
                text = { Text(item) },
                onClick = { tabClicker(index) },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    pagerState: PagerState,
    items: List<Int>,
    onTabClick: suspend (index: Int) -> Unit = { pagerState.animateScrollToPage(it) },
) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val tabClicker: (Int) -> Unit = remember { { scope.launch { onTabClick(it) } } }

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = indicator(pagerState),
        divider = {},
        modifier = Modifier.horizontalInsetsPadding()
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                selected = pagerState.currentPage == index,
                text = { Text(stringResource(item)) },
                onClick = { tabClicker(index) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }
}
