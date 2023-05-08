package com.san.kir.schedule.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.ScrollableTabs
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.topBar
import com.san.kir.schedule.R
import com.san.kir.schedule.utils.pages
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MainScreen(
    navigateUp: () -> Unit,
    navigateToItem: (Long, SharedParams) -> Unit,
) {
    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.schedule),
            actions = {
                val params = rememberSharedParams(fromCenter = true)
                MenuIcon(
                    icon = Icons.Default.Add,
                    modifier = Modifier.saveParams(params),
                    onClick = { navigateToItem(-1L, params) }
                )
            },
        ),
        additionalPadding = 0.dp
    ) {
        val pagerState = rememberPagerState()
        val pages = pages()

        ScrollableTabs(
            pagerState,
            items = pages.map { stringResource(it.nameId) }.toPersistentList()
        )

        HorizontalPager(
            pageCount = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            pages[index].content(navigateToItem)
        }
    }
}
