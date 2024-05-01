package com.san.kir.chapters.ui.chapters

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.san.kir.chapters.R
import com.san.kir.chapters.utils.AboutPageContent
import com.san.kir.chapters.utils.ListPageContent
import com.san.kir.chapters.utils.topBar
import com.san.kir.core.compose.ScreenPadding
import com.san.kir.core.compose.Tabs
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ChaptersScreen(
    navigateUp: () -> Unit,
    navigateToViewer: (Long) -> Unit,
    mangaId: Long,
) {
    val holder: ChaptersStateHolder = stateHolder { ChaptersViewModel() }
    val state by holder.state.collectAsState()

    LaunchedEffect(Unit) { holder.sendAction(ChaptersEvent.Set(mangaId)) }

    val pagerState = rememberPagerState()

    ScreenPadding(
        topBar = topBar(
            selectedCount = state.selectionCount,
            selectionMode = state.selectionMode,
            backgroundAction = state.backgroundAction,
            manga = state.manga,
            navigateUp = navigateUp,
            sendEvent = holder::sendAction
        ),
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            val pages = chapterPages(state.manga.isAlternativeSite)

            if (state.showTitle) Tabs(pagerState, pages)

            HorizontalPager(
                pageCount = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f),
                key = { it }
            ) { index ->
                when (index) {
                    0 -> AboutPageContent(
                        nextChapter = state.nextChapter,
                        logo = state.manga.logo,
                        readCount = state.readCount,
                        count = state.count,
                        navigateToViewer = navigateToViewer
                    )

                    1 -> ListPageContent(
                        chapterFilter = state.chapterFilter,
                        selectionMode = state.selectionMode,
                        items = state.items,
                        navigateToViewer = navigateToViewer,
                        sendEvent = holder::sendAction,
                    )
                }
            }
        }
    }
}

@Composable
internal fun chapterPages(isAlternative: Boolean): List<Int> {
    return remember(isAlternative) {
        if (isAlternative)
            emptyList(R.string.list_chapters_page_about)
        else
            emptyList(R.string.list_chapters_page_about, R.string.list_chapters_page_list)
    }
}
