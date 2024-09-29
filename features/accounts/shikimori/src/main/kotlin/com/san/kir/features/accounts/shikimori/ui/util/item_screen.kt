package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.ThemedPreview
import com.san.kir.core.compose.ThemedPreviewContainer
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem
import com.san.kir.features.accounts.shikimori.logic.useCases.CanBind
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncDialogEvent
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncDialogState
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncItem
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncItemState
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncManagerAction
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncState

@Composable
internal fun ColumnScope.SyncStateContent(
    state: SyncState,
    findTextId: Int,
    okTextId: Int,
    foundsTextId: Int,
    notFoundsTextId: Int,
    notFoundsSearchTextId: Int,
    sendAction: (Action) -> Unit,
    onSearch: (String) -> Unit,
    canApply: Boolean = true,
) {
    when (state) {
        // Поиск в базе данных, подходящей по названию манги
        SyncState.Finding -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.default),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(modifier = Modifier.padding(Dimensions.default))
            Text(
                stringResource(findTextId),
                modifier = Modifier.padding(start = Dimensions.default),
            )
        }
        // Уже имеется связанная манга
        is SyncState.Ok -> {
            ItemHeader(okTextId)
            MangaItemContent(
                avatar = state.manga.logo,
                mangaName = state.manga.name,
                readingChapters = state.manga.read,
                allChapters = state.manga.all,
                currentStatus = state.manga.status,
                canBind = CanBind.Already,
                onClick = { sendAction(SyncManagerAction.CancelSync(state.manga)) })
        }
        // Список подходящей манги
        is SyncState.Founds -> {
            ItemHeader(foundsTextId)
            SyncItem(
                state.items.first(),
                modifier = Modifier.bottomInsetsPadding(),
                canHide = false,
                canApply = canApply,
                sendAction = sendAction,
            )
        }
        // Поиск ничего не дал
        is SyncState.NotFound -> {
            DefaultSpacer()

            ItemHeader(notFoundsTextId)
            ItemHeader(notFoundsSearchTextId)

            HalfSpacer()

            Button(onClick = { onSearch(state.name) }) {
                Text(stringResource(R.string.local_search_not_founds_go))
            }
        }

        else -> Unit
    }
}

// Диалоги появляющиеся в спорных ситуациях
@Composable
internal fun DialogsSyncState(
    state: SyncDialogState,
    onSendEvent: (SyncDialogEvent) -> Unit,
) {
    when (state) {
        SyncDialogState.None -> {
        }

        is SyncDialogState.Init -> {
            AlertDialog(
                onDismissRequest = { onSendEvent(SyncDialogEvent.DialogDismiss) },
                title = { Text(stringResource(R.string.local_search_dialog_diff_title)) },
                text = { Text(stringResource(R.string.local_search_dialog_bind_items)) },
                confirmButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.SyncNext(false)) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.DialogDismiss) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_no))
                    }
                }
            )
        }

        // Разное количество глав
        is SyncDialogState.DifferentChapterCount -> {
            AlertDialog(
                onDismissRequest = { onSendEvent(SyncDialogEvent.DialogDismiss) },
                title = { Text(stringResource(R.string.local_search_dialog_diff_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.local_search_dialog_diffall_text, state.local, state.online
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.SyncNext(false)) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.DialogDismiss) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_no))
                    }
                }
            )
        }
        // Разное количество прочитанных глав
        is SyncDialogState.DifferentReadCount -> {
            AlertDialog(
                onDismissRequest = { onSendEvent(SyncDialogEvent.DialogDismiss) },
                title = { Text(stringResource(R.string.local_search_dialog_diff_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.local_search_dialog_diffread_text, state.local, state.online
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.SyncNext(false)) }) {
                        Text(stringResource(R.string.local_search_dialog_diffread_local))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.SyncNext(true)) }) {
                        Text(stringResource(R.string.local_search_dialog_diffread_online))
                    }
                }
            )
        }
        // Отмена привязки
        is SyncDialogState.CancelSync -> {
            AlertDialog(
                onDismissRequest = { onSendEvent(SyncDialogEvent.DialogDismiss) },
                title = { Text(stringResource(R.string.local_search_dialog_diff_title)) },
                text = { Text(stringResource(R.string.local_search_dialog_cancelsync_text)) },
                confirmButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.SyncCancel(state.rate)) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onSendEvent(SyncDialogEvent.DialogDismiss) }) {
                        Text(stringResource(R.string.local_search_dialog_diffall_no))
                    }
                }
            )
        }
    }
}

@ThemedPreview
@Composable
private fun BodyPreview() {
    val values = sequenceOf(
        SyncState.Empty,
        SyncState.Finding,
        SyncState.Founds(
            listOf(
                SyncItemState(
                    LibraryMangaItem(
                        id = 1L,
                        name = "Library Name",
                        sort = false,
                        read = 5,
                        all = 15
                    ),
                    listOf(
                        AccountMangaItem(
                            id = 2L,
                            name = "Account Name",
                            read = 9,
                            all = 20,
                            volumes = 0,
                        )
                    ),
                )
            )
        ),
        SyncState.NotFound("test"),
        SyncState.Ok(object : MangaItem {
            override val id = 1L
            override val name = "Synced Manga Name"
            override val logo = ""
            override val read = 23
            override val all = 43
            override val description = ""
        })
    )

    ThemedPreviewContainer {
        Column {
            values.forEach { state ->
                SyncStateContent(
                    state,
                    R.string.local_search_searching,
                    R.string.local_search_sync,
                    R.string.local_search_founds,
                    R.string.local_search_not_founds,
                    R.string.local_search_not_founds_ex,
                    { },
                    { },
                    false
                )
                HorizontalDivider()
            }
        }
    }
}

