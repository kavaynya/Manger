package com.san.kir.features.shikimori.ui.accountScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenPadding
import com.san.kir.core.compose.ToolbarProgress
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.features.shikimori.R
import com.san.kir.features.shikimori.logic.BackgroundTasks
import com.san.kir.features.shikimori.logic.useCases.CanBind
import com.san.kir.features.shikimori.ui.accountItem.LoginState
import com.san.kir.features.shikimori.ui.util.LogOutDialog
import com.san.kir.features.shikimori.ui.util.MangaItemContent
import com.san.kir.features.shikimori.ui.util.TextLoginOrNot

@Composable
internal fun AccountScreen(
    navigateUp: () -> Unit,
    navigateToShikiItem: (id: Long, params: SharedParams) -> Unit,
    navigateToLocalItems: (SharedParams) -> Unit,
    navigateToSearch: () -> Unit,
) {
    val holder: AccountStateHolder = stateHolder { AccountViewModel() }
    val state by holder.state.collectAsState()

    ScreenPadding(
        topBar = topBar(
            onSendEvent = holder::sendAction,
            navigateUp = navigateUp,
            navigateToSearch = navigateToSearch,
            state = state.login,
            hasAction = state.action
        ),
        additionalPadding = Dimensions.zero,
        fab = {
            if (state.login is LoginState.LogInOk) {
                val params = rememberSharedParams()
                FloatingActionButton(
                    onClick = { navigateToLocalItems(params) },
                    modifier = Modifier.saveParams(params)
                ) {
                    Icon(Icons.Default.LocalLibrary, contentDescription = "local library")
                }
            }
        },
        onRefresh = { holder.sendAction(AccountEvent.Update) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                )
                .imePadding()
        ) {
            CatalogContent(state.items, navigateToShikiItem)
        }
    }

    LogOutDialog(
        state = state.dialog,
        onDismiss = { holder.sendAction(AccountEvent.CancelLogOut) },
        onConfirm = { holder.sendAction(AccountEvent.LogOut) }
    )
}

@Composable
private fun topBar(
    onSendEvent: (AccountEvent) -> Unit,
    navigateUp: () -> Unit,
    navigateToSearch: () -> Unit,
    state: LoginState,
    hasAction: BackgroundTasks,
) = topBar(
    navigationButton = NavigationButton.Back(navigateUp),
    title = stringResource(R.string.site_name),
    subtitleContent = { TextLoginOrNot(state) },
    actions = {
        when (state) {
            is LoginState.LogInOk -> {
                MenuIcon(icon = Icons.Default.Search, onClick = navigateToSearch)

                ExpandedMenu {
                    MenuText(R.string.update_data, onClick = { onSendEvent(AccountEvent.Update) })
                    MenuText(R.string.logout, onClick = { onSendEvent(AccountEvent.LogOut) })
                }
            }

            LoginState.Loading -> ToolbarProgress()
            else -> {}
        }

    },
    hasAction = if (state is LoginState.LogInOk) hasAction.loading || hasAction.checkBind else false,
    progressAction = hasAction.progress
)

@Composable
private fun CatalogContent(
    state: ScreenItems,
    navigateToItem: (id: Long, params: SharedParams) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        if (state.bind.isNotEmpty()) {
            stickyHeader(
                textRes = R.string.synced_catalog_items,
                count = state.bind.size
            )

            items(state.bind, key = { item -> item.id }) { item ->
                MangaItemContent(
                    avatar = item.logo,
                    mangaName = item.name,
                    readingChapters = item.read,
                    allChapters = item.all,
                    currentStatus = item.status,
                    canBind = CanBind.Already,
                    onClick = { navigateToItem(item.id, it) }
                )
            }
        }
        if (state.unBind.isNotEmpty()) {
            stickyHeader(
                textRes = R.string.nonsynced_catalog_items,
                count = state.unBind.size,
                secondaryCount = state.unBind.count { (_, bind) -> bind == CanBind.Ok }
            )

            items(
                state.unBind,
                key = { item -> item.item.id },
                contentType = { item -> item.status }
            ) { (item, canBind) ->
                MangaItemContent(
                    avatar = item.logo,
                    mangaName = item.name,
                    readingChapters = item.read,
                    allChapters = item.all,
                    currentStatus = item.status,
                    canBind = canBind,
                    onClick = { navigateToItem(item.id, it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.stickyHeader(
    textRes: Int,
    count: Int,
    secondaryCount: Int = 0,
) {
    stickyHeader {
        Card(elevation = Dimensions.half, modifier = Modifier.padding(Dimensions.quarter)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.quarter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(textRes, count))

                if (secondaryCount > 0) {
                    Text(
                        "-$secondaryCount",
                        modifier = Modifier
                            .padding(horizontal = Dimensions.half)
                            .background(
                                color = Color.Magenta,
                                shape = CircleShape
                            )
                            .padding(horizontal = Dimensions.quarter),
                    )
                }
            }
        }
    }
}