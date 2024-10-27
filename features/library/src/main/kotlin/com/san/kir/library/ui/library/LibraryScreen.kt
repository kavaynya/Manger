package com.san.kir.library.ui.library

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.BottomSheets
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.ScrollableTabs
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.compose.topInsetsPadding
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.SimplifiedManga
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.library.R
import com.san.kir.library.ui.drawer.DrawerScreen
import com.san.kir.library.utils.LibraryContent
import com.san.kir.library.utils.LibraryDropUpMenu
import com.san.kir.library.utils.libraryActions
import com.san.kir.library.utils.libraryMenu

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryScreen(navigation: LibraryNavigation) {
    val holder: LibraryStateHolder = stateHolder { LibraryViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()
    val selectMangaState = rememberDialogState<SimplifiedManga>()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val pagerState = rememberPagerState { (state.items as? ItemsState.Ok)?.names?.size ?: 0 }
    val hasTabs by remember { derivedStateOf { state.items is ItemsState.Ok } }

    holder.OnEvent { event ->
        when (event) {
            is LibraryEvent.ToInfo -> navigation.toInfo(event.id, event.params)
            is LibraryEvent.ToStorage -> navigation.toStorage(event.id, event.params)
            is LibraryEvent.ToStats -> navigation.toStats(event.id, event.params)
            is LibraryEvent.ToChapters -> navigation.toChapters(event.id, event.params)
            is LibraryEvent.ToOnline -> navigation.toOnline(event.params)
            is LibraryEvent.ToScreen -> navigation.toScreen(event.menu)
            is LibraryEvent.ShowSelectedMangaDialog -> selectMangaState.show(event.item)
            is LibraryEvent.DismissSelectedMangaDialog -> selectMangaState.dismiss()
        }
    }

    ScreenContent(
        drawerState = drawerState,
        topBar = topBar(
            title = stringResource(R.string.library),
            actions = libraryActions(navigation.toOnline),
            navigationButton = NavigationButton.Scaffold(drawerState),
            hasAction = state.background == BackgroundState.Work,
            additionalContent = {
                TopAnimatedVisibility(visible = hasTabs) {
                    val names = (state.items as ItemsState.Ok).names
                    ScrollableTabs(
                        pagerState = pagerState,
                        items = names,
                        modifier = Modifier.horizontalInsetsPadding(),
                        onTabClick = pagerState::animateScrollToPage
                    )
                }
            }
        ),
        menuActions = libraryMenu(sendAction),
        additionalPadding = Dimensions.zero,
        drawerContent = { DrawerScreen(navigation.toScreen) },
    ) {
        when (val currentState = state.items) {
            ItemsState.Empty -> Empty { navigation.toScreen(MainMenuType.Category) }
            ItemsState.Load -> Loading()
            is ItemsState.Ok -> LibraryContent(pagerState, state, currentState, sendAction)
        }
    }

    BottomSheets(
        selectMangaState,
        modifier = Modifier.topInsetsPadding(),
        animationSpec = spring(0.9f, 600f)
    ) {
        LibraryDropUpMenu(
            itemsState = state.items,
            selectedManga = it,
            sendAction = holder::sendAction
        )
    }
}

@Composable
private fun ColumnScope.Loading() {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ColumnScope.Empty(navigateToCategories: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(stringResource(R.string.library_no_categories))
            Button(onClick = navigateToCategories) {
                Text(stringResource(R.string.go_to_categories))
            }
        }
    }
}
