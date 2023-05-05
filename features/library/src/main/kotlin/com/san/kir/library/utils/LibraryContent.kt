package com.san.kir.library.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.san.kir.core.compose.ScrollableTabs
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.library.ui.library.ItemsState
import com.san.kir.library.ui.library.LibraryEvent
import com.san.kir.library.ui.library.LibraryNavigation
import com.san.kir.library.ui.library.LibraryState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ColumnScope.LibraryContent(
    navigation: LibraryNavigation,
    state: LibraryState,
    itemsState: ItemsState.Ok,
    sendEvent: (LibraryEvent) -> Unit
) {
    val pagerState = rememberPagerState()

    LaunchedEffect(itemsState, pagerState.currentPage) {
        if (pagerState.currentPage >= itemsState.items.size)
            pagerState.animateScrollToPage(0)
        if (itemsState.items.isNotEmpty())
            sendEvent(LibraryEvent.SetCurrentCategory(itemsState.items[pagerState.currentPage]))
    }

    if (pagerState.currentPage < itemsState.items.size) {

        val dragged by pagerState.interactionSource.collectIsDraggedAsState()
        if (dragged) sendEvent(LibraryEvent.NonSelect)

        // Название вкладок
        ScrollableTabs(
            pagerState = pagerState,
            items = itemsState.names,
            modifier = Modifier.horizontalInsetsPadding()
        ) {
            sendEvent(LibraryEvent.NonSelect)
            pagerState.animateScrollToPage(it)
        }

        // Перелистываемые вкладки
        HorizontalPager(
            pageCount = itemsState.names.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, true),
            key = { itemsState.items[it].id }
        ) { index ->
            LibraryPage(
                navigation = navigation,
                item = itemsState.items[index],
                showCategory = state.showCategory,
                sendEvent = sendEvent,
            )
        }
    }
}
