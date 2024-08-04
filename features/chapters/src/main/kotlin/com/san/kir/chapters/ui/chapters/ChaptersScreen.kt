package com.san.kir.chapters.ui.chapters

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.san.kir.chapters.R
import com.san.kir.chapters.utils.AboutPageContent
import com.san.kir.chapters.utils.DefaultModeActions
import com.san.kir.chapters.utils.DeleteSelectedChaptersAlertDialog
import com.san.kir.chapters.utils.FullDeleteChaptersAlertDialog
import com.san.kir.chapters.utils.FullResetAlertDialog
import com.san.kir.chapters.utils.ListPageContent
import com.san.kir.chapters.utils.defaultMenuActions
import com.san.kir.chapters.utils.selectionModeColor
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenClear
import com.san.kir.core.compose.ScrollableTabs
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.navigation.show
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ChaptersScreen(
    navigateUp: () -> Unit,
    navigateToViewer: (Long) -> Unit,
    navigateToGlobalSearch: (String) -> Unit,
    mangaId: Long,
) {
    val holder: ChaptersStateHolder = stateHolder { ChaptersViewModel(mangaId) }
    val state by holder.state.collectAsStateWithLifecycle()
    val nextChapter by holder.nextChapter.collectAsStateWithLifecycle()
    val selectionMode by holder.selectionMode.collectAsStateWithLifecycle()
    val itemsContent by holder.itemsContent.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    val pages = chapterPages(state.manga.isAlternativeSite)
    val pagerState = rememberPagerState(pageCount = { pages.size })

    val dialogFullReset = rememberDialogState<EmptyDialogData>(true) {
        holder.sendAction(ChaptersAction.FullReset)
    }
    val dialogDeleteSelected = rememberDialogState<EmptyDialogData>(true) {
        holder.sendAction(ChaptersAction.WithSelected(Selection.DeleteFiles))
    }
    val dialogFullDelete = rememberDialogState<EmptyDialogData>(true) {
        holder.sendAction(ChaptersAction.WithSelected(Selection.DeleteFromDB))
    }

    holder.OnEvent { event ->
        when (event) {
            is ChaptersEvent.ShowFullResetDialog -> dialogFullReset.show()
            is ChaptersEvent.ShowDeleteDialog -> dialogDeleteSelected.show()
            is ChaptersEvent.ShowFullDeleteDialog -> dialogFullDelete.show()
            is ChaptersEvent.ToGlobalSearch -> navigateToGlobalSearch(state.manga.name)
            is ChaptersEvent.ToViewer -> navigateToViewer(event.id)
        }
    }

    ScreenClear(
        topBar = topBar(
            title = if (selectionMode.enabled) state.manga.name
            else pluralStringResource(
                R.plurals.selected_format,
                selectionMode.selectionCount,
                selectionMode.selectionCount
            ),
            navigationButton = if (selectionMode.enabled) {
                NavigationButton.Close { sendAction(ChaptersAction.WithSelected(Selection.Clear)) }
            } else {
                NavigationButton.Back(navigateUp)
            },
            hasAction = state.backgroundAction,
            actions = {
                StartAnimatedVisibility(selectionMode.enabled.not()) {
                    DefaultModeActions(isUpdate = state.manga.isUpdate, sendAction = sendAction)
                }
            },
            additionalContent = {
                TopAnimatedVisibility(visible = state.showTitle && selectionMode.enabled.not()) {
                    ScrollableTabs(pagerState = pagerState, items = pages)
                }
            },
            contentColor = selectionModeColor(state = selectionMode.enabled),
        ),
        menuActions = defaultMenuActions(
            isUpdate = state.manga.isUpdate,
            isAlternativeSort = state.manga.isAlternativeSort,
            sendAction = sendAction
        )
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it },
            userScrollEnabled = selectionMode.enabled.not()
        ) { index ->
            when (index) {
                0 -> AboutPageContent(
                    nextChapter = nextChapter,
                    logo = state.manga.logo,
                    color = state.manga.color,
                    readCount = itemsContent.readCount,
                    count = itemsContent.count,
                    hasReading = itemsContent.hasReadingChapters,
                    errorState = state.error,
                    sendAction = sendAction,
                )

                1 -> ListPageContent(
                    chapterFilter = state.chapterFilter,
                    selectionMode = selectionMode,
                    itemsContent = itemsContent,
                    sendAction = sendAction,
                )
            }
        }
    }

    FullResetAlertDialog(resetDialogState = dialogFullReset)
    DeleteSelectedChaptersAlertDialog(state = dialogDeleteSelected)
    FullDeleteChaptersAlertDialog(state = dialogFullDelete)
}

@Composable
internal fun chapterPages(isAlternative: Boolean): List<String> {
    val about = stringResource(id = R.string.about)
    val list = stringResource(id = R.string.content)
    return remember(isAlternative) { if (isAlternative) listOf(about) else listOf(about, list) }
}
