package com.san.kir.features.accounts.shikimori.ui.accountScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FabButton
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.ToolbarProgress
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.navigation.show
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.BackgroundTasks
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.useCases.BindStatus
import com.san.kir.features.accounts.shikimori.logic.useCases.CanBind
import com.san.kir.features.accounts.shikimori.ui.accountItem.LoginState
import com.san.kir.features.accounts.shikimori.ui.util.LogOutDialog
import com.san.kir.features.accounts.shikimori.ui.util.MangaItemContent
import com.san.kir.features.accounts.shikimori.ui.util.textLoginOrNot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountScreen(
    accountId: Long,
    navigateUp: () -> Unit,
    navigateToShikiItem: (item: AccountMangaItem, params: SharedParams) -> Unit,
    navigateToLocalItems: (SharedParams) -> Unit,
    navigateToSearch: () -> Unit,
) {
    val holder: AccountStateHolder = stateHolder { AccountViewModel(accountId) }
    val state by holder.state.collectAsStateWithLifecycle()
    val boundedItems by holder.boundedItems.collectAsStateWithLifecycle()
    val unboundedItems by holder.unboundedItems.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    val logOutDialog = rememberDialogState<EmptyDialogData>(
        onNeutral = { sendAction(AccountAction.LogOut(false)) },
        onSuccess = { sendAction(AccountAction.LogOut(true)) },
    )

    holder.OnEvent { event ->
        when (event) {
            is AccountEvent.ToBack -> navigateUp()
            is AccountEvent.ToSearch -> navigateToSearch()
            is AccountEvent.ToItem -> navigateToShikiItem(event.item, event.params)
            is AccountEvent.ShowLogOutDialog -> logOutDialog.show()
        }
    }

    ScreenList(
        topBar = topBar(state.login, state.action, sendAction),
        additionalPadding = Dimensions.zero,
        bottomContent = {
            if (state.login is LoginState.Ok) {
                val params = rememberSharedParams()
                FabButton(
                    onClick = { navigateToLocalItems(params) },
                    modifier = Modifier.saveParams(params),
                    image = Icons.Default.LocalLibrary,
                )
            }
        },
    ) {
        if (boundedItems.isNotEmpty()) {
            boundedContent(boundedItems, sendAction)
        }

        if (unboundedItems.isNotEmpty()) {
            unboundedContent(unboundedItems, sendAction)
        }
    }

    LogOutDialog(logOutDialog)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun topBar(
    state: LoginState,
    hasAction: BackgroundTasks,
    sendAction: (Action) -> Unit,
) = topBar(
    navigationButton = NavigationButton.Back { sendAction(ReturnEvents(AccountEvent.ToBack)) },
    title = stringResource(R.string.site_name),
    subtitle = textLoginOrNot(state),
    actions = {
        when (state) {
            is LoginState.Ok -> {
                MenuIcon(icon = Icons.Default.Search) { sendAction(ReturnEvents(AccountEvent.ToSearch)) }
                MenuIcon(
                    icon = rememberImage(state.logo),
                    onClick = { sendAction(ReturnEvents(AccountEvent.ShowLogOutDialog)) },
                )
            }

            LoginState.Loading -> ToolbarProgress()
            else -> {}
        }

    },
    hasAction = if (state is LoginState.Ok) hasAction.loading || hasAction.checkBind else false,
    progressAction = hasAction.progress
)

private fun LazyListScope.boundedContent(
    list: List<AccountMangaItem>,
    sendAction: (Action) -> Unit
) {
    stickyHeader(textRes = R.string.synced_catalog_items, count = list.size)

    items(list, key = { item -> item.id }) { item ->
        MangaItemContent(
            avatar = item.logo,
            mangaName = item.name,
            readingChapters = item.read,
            allChapters = item.all,
            currentStatus = item.status,
            canBind = CanBind.Already,
            onClick = { sendAction(ReturnEvents(AccountEvent.ToItem(item, it))) },
        )
    }
}

private fun LazyListScope.unboundedContent(
    list: List<BindStatus<AccountMangaItem>>,
    sendAction: (Action) -> Unit
) {
    stickyHeader(
        textRes = R.string.nonsynced_catalog_items,
        count = list.size,
        secondaryCount = list.count { bind-> bind.status == CanBind.Ok }
    )

    items(
        list,
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
            onClick = { sendAction(ReturnEvents(AccountEvent.ToItem(item, it))) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.stickyHeader(
    textRes: Int,
    count: Int,
    secondaryCount: Int = 0,
) {
    stickyHeader {
        Card(modifier = Modifier.padding(Dimensions.quarter)) {
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
                            .background(color = Color.Magenta, shape = CircleShape)
                            .padding(horizontal = Dimensions.quarter),
                    )
                }
            }
        }
    }
}
