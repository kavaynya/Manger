package com.san.kir.schedule.ui.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.ScrollableTabs
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.topBar
import com.san.kir.schedule.R
import com.san.kir.schedule.utils.pages


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleScreen(
    navigateUp: () -> Unit,
    navigateToItem: (Long, SharedParams) -> Unit,
) {
    val pages = pages()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.schedule),
            additionalContent = {
                ScrollableTabs(
                    pagerState = pagerState,
                    items = pages.map { stringResource(it.nameId) }
                )
            }
        ),
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = 1,
            key = { it }
        ) { index ->
            pages[index].content(navigateToItem)
        }
    }
}
