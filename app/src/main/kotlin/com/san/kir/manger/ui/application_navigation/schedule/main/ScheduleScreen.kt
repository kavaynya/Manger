package com.san.kir.manger.ui.application_navigation.schedule.main

import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.san.kir.manger.R
import com.san.kir.manger.ui.application_navigation.schedule.ScheduleNavTarget
import com.san.kir.core.compose_utils.MenuIcon
import com.san.kir.core.compose_utils.ScrollableTabs
import com.san.kir.manger.utils.compose.navigate
import com.san.kir.core.compose_utils.TopBarScreenContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulesScreen(nav: NavHostController) {
    TopBarScreenContent(
        navHostController = nav,
        title = stringResource(R.string.main_menu_schedule),
        actions = {
            MenuIcon(
                icon = Icons.Default.Add,
                onClick = { nav.navigate(ScheduleNavTarget.Schedule, -1L) })

        },
        additionalPadding = 0.dp
    ) {
        val pagerState = rememberPagerState()
        val pages = schedulePages()

        ScrollableTabs(pagerState,
            items =pages.map { stringResource(it.nameId) }
        )

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            pages[index].content(nav)
        }
    }
}