package com.san.kir.features.accounts.shikimori.ui.localItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenClear
import com.san.kir.core.compose.horizontalAndBottomInsetsPadding
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncState
import com.san.kir.features.accounts.shikimori.ui.util.Chapters
import com.san.kir.features.accounts.shikimori.ui.util.Description
import com.san.kir.features.accounts.shikimori.ui.util.MangaNames

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocalItemScreen(
    accountId: Long,
    mangaId: Long,
    navigateUp: () -> Unit,
    navigateToSearch: (String) -> Unit,
) {
    val holder: LocalItemStateHolder = stateHolder { LocalItemViewModel(accountId, mangaId) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    ScreenClear(
        topBar = topBar(
            title = stringResource(R.string.profile_item_title),
            navigationButton = NavigationButton.Back(navigateUp),
            hasAction = state.manga is MangaState.Load
        ),
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = Dimensions.default),
            contentPadding = horizontalAndBottomInsetsPadding()
        ) {
            when (val manga = state.manga) {
                is MangaState.Ok -> content(manga, state.sync, sendAction, navigateToSearch)

                MangaState.Error -> {}
                MangaState.Load -> {}
            }
        }
    }
}

private fun LazyListScope.content(
    manga: MangaState.Ok,
    sync: SyncState,
    sendEvent: (LocalItemAction) -> Unit,
    navigateToSearch: (String) -> Unit,
) {
    item { MangaNames(manga.item.name) }
    item { Chapters(manga.item.all, manga.item.read) }
    item { AdditionalInfo(manga.item) }
    item { HorizontalDivider(Modifier.padding(vertical = Dimensions.default)) }
}

// Дополнительная информация о манге
@Composable
private fun AdditionalInfo(manga: LibraryMangaItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.default)
    ) {
        Image(
            rememberImage(manga.logo),
            contentDescription = "manga logo",
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = Dimensions.default),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(3f)) {
            Description(manga.description)
        }
    }
}

