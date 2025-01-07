package com.san.kir.chapters.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.san.kir.chapters.R
import com.san.kir.chapters.ui.chapters.ChaptersAction
import com.san.kir.core.compose.ExpandedMenuScope
import com.san.kir.core.compose.TopBarActions
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.utils.viewModel.Action

@Composable
internal fun TopBarActions.DefaultModeActions(
    isUpdate: Boolean,
    sendAction: (ChaptersAction) -> Unit,
) {
    Row {
        StartAnimatedVisibility(isUpdate) {
            MenuIcon(Icons.Default.Update) { sendAction(ChaptersAction.UpdateManga) }
        }

        ExpandedMenu()
    }
}

@Composable
internal fun defaultMenuActions(
    isUpdate: Boolean,
    isAlternativeSort: Boolean,
    notReadCount: Int,
    allCount: Int,
    sendAction: (Action) -> Unit,
): @Composable ExpandedMenuScope.() -> Unit = {
    if (notReadCount > 0) {
        MenuText(R.string.download_next) { sendAction(ChaptersAction.DownloadNext) }
        MenuText(stringResource(R.string.download_not_reading, notReadCount)) {
            sendAction(ChaptersAction.DownloadNotRead)
        }
    }
    if (allCount > 0) {
        MenuText(stringResource(R.string.download_all, allCount)) { sendAction(ChaptersAction.DownloadAll) }
    }

    CheckedMenuText(R.string.allow_updates, isUpdate) { sendAction(ChaptersAction.ChangeIsUpdate) }
    CheckedMenuText(R.string.change_sorting, isAlternativeSort) {
        sendAction(ChaptersAction.ChangeMangaSort)
    }
}
