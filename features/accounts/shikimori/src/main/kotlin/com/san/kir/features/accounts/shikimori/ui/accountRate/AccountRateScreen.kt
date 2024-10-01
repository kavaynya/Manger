package com.san.kir.features.accounts.shikimori.ui.accountRate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.ThemedPreview
import com.san.kir.core.compose.ThemedPreviewContainer
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncState
import com.san.kir.features.accounts.shikimori.ui.util.AdditionalInfo
import com.san.kir.features.accounts.shikimori.ui.util.ChangeDialog
import com.san.kir.features.accounts.shikimori.ui.util.Description
import com.san.kir.features.accounts.shikimori.ui.util.MangaNames
import com.san.kir.features.accounts.shikimori.ui.util.SyncStateContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountRateScreen(
    navigateUp: () -> Unit,
    navigateToSearch: (String) -> Unit,
    accountId: Long,
    mangaItem: AccountMangaItem,
) {
    val holder: AccountRateStateHolder = stateHolder { AccountRateViewModel(accountId, mangaItem) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()
    val changeItemState = rememberDialogState<AccountMangaItem>()

    holder.OnEvent { event ->
        when (event) {
            is AccountRateEvent.ShowChangeDialog -> changeItemState.show(event.item)
            is AccountRateEvent.ToSearch -> navigateToSearch(event.query)
        }
    }

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = state.item.name.ifEmpty { stringResource(R.string.profile_item_title) },
            actions = {
                if (state.item.inAccount) {
                    MenuIcon(
                        icon = Icons.Default.Delete,
                        onClick = { sendAction(AccountRateAction.Remove) }
                    )
                } else {
                    MenuIcon(
                        icon = Icons.Default.Add,
                        onClick = { sendAction(AccountRateAction.Add) }
                    )
                }
            },
            hasAction = state.hasLoading
        ),
    ) {
        TopAnimatedVisibility(state.itemState is ItemState.Error) { ErrorContent(sendAction) }
        OkContent(state.item, state.sync, sendAction)
    }

    ChangeDialog(changeItemState) { sendAction(AccountRateAction.Change(it)) }
}


@Composable
private fun ColumnScope.OkContent(
    item: AccountMangaItem,
    sync: SyncState,
    sendAction: (Action) -> Unit
) {
    DefaultSpacer()

    MangaNames(
        firstName = if (item.name == item.russian) "" else item.russian,
        secondName = item.english
    )

    AdditionalInfo(item, sendAction)

    Description(item.inAccount, item.description)

    HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.default))

    SyncStateContent(
        state = sync,
        findTextId = R.string.local_search_searching,
        okTextId = R.string.local_search_sync,
        foundsTextId = R.string.local_search_founds,
        notFoundsTextId = R.string.local_search_not_founds,
        notFoundsSearchTextId = R.string.local_search_not_founds_ex,
        sendAction = sendAction,
        onSearch = { sendAction(ReturnEvents(AccountRateEvent.ToSearch(it))) }
    )
}

@Composable
private fun ColumnScope.ErrorContent(sendAction: (Action) -> Unit) {

    DefaultSpacer()

    Text(
        text = stringResource(R.string.error),
        modifier = Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.titleLarge
    )

    DefaultSpacer()

    Button(
        onClick = { sendAction(AccountRateAction.TryAgainLastAction) },
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(R.string.try_again))
    }

    DefaultSpacer()

}

@ThemedPreview
@Composable
private fun ContentPreview() {
    val item = AccountMangaItem(
        name = "Test Account Manga Name",
        english = "Test Account Manga Name",
        russian = "Test Account Manga Name",
        read = 342,
        all = 487,
        volumes = 100,
        idInLibrary = 2L,
        idInAccount = 2L,
        mangaScore = 3.8f,
        userScore = 6,
        rewatches = 0,
    )
    val state = SyncState.Error

    ThemedPreviewContainer {
        Column(modifier = Modifier.fillMaxWidth()) {
            ErrorContent {}

            HorizontalDivider()

            OkContent(item, state) {}
        }
    }
}
