package com.san.kir.library.ui.library

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ScaffoldState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.coroutines.mainLaunch
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.library.R
import com.san.kir.library.ui.drawer.DrawerScreen
import com.san.kir.library.utils.BottomDialog
import com.san.kir.library.utils.LibraryContent
import com.san.kir.library.utils.LibraryDropUpMenu
import com.san.kir.library.utils.libraryActions

@Composable
internal fun LibraryScreen(navigation: LibraryNavigation) {
    val holder: LibraryStateHolder = stateHolder { LibraryViewModel() }
    val state by holder.state.collectAsState()

    val unSelect = rememberLambda { holder.sendAction(LibraryEvent.NonSelect) }

    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    BottomDialog(
        state = state.selectedManga,
        onDismiss = unSelect,
        modifier = Modifier.fillMaxSize(),
        dialogContent = {
            LibraryDropUpMenu(
                navigateToInfo = navigation.navigateToInfo,
                navigateToStorage = navigation.navigateToStorage,
                navigateToStats = navigation.navigateToStats,
                itemsState = state.items,
                selectedManga = it,
                sendEvent = holder::sendAction
            )
        }
    ) {
        ScreenContent(
            topBar = topBar(
                title = stringResource(R.string.library_title),
                actions = libraryActions(navigation.navigateToOnline, holder::sendAction),
                navigationButton = NavigationButton.Scaffold(scaffoldState),
                hasAction = state.background is BackgroundState.Work
            ),
            additionalPadding = Dimensions.zero,
            scaffoldState = scaffoldState,
            drawerContent = { DrawerScreen(navigation.navigateToScreen) },
            enableCollapsingBars = false,
        ) {
            when (val currentState = state.items) {
                ItemsState.Empty ->
                    Empty {
                        navigation.navigateToScreen(MainMenuType.Category)
                    }

                ItemsState.Load ->
                    Loading()

                is ItemsState.Ok -> {
                    LibraryContent(
                        navigation = navigation,
                        state = state,
                        itemsState = currentState,
                        sendEvent = holder::sendAction
                    )
                }
            }
        }
    }

//    BackHandler(scaffoldState)
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
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(text = stringResource(id = R.string.library_no_categories))
            Button(onClick = navigateToCategories) {
                Text(text = stringResource(id = R.string.library_to_categories))
            }
        }
    }
}

@Composable
private fun BackHandler(scaffoldState: ScaffoldState) {

    val context = LocalContext.current as? ComponentActivity
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        coroutineScope.mainLaunch {
            if (scaffoldState.drawerState.isOpen) {
                scaffoldState.drawerState.close()
            } else {
                context?.finish()
            }
        }
    }
}
