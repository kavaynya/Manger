package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.runtime.Composable
import com.san.kir.core.compose.AlertDialog
import com.san.kir.core.utils.navigation.DialogState
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.features.accounts.shikimori.R


@Composable
internal fun LogOutDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        title = R.string.logout_dialog_title,
        text = R.string.logout_dialog_text,
        negative = R.string.logout_dialog_cancel,
        positive = R.string.logout_dialog_ok_full,
        neutral = R.string.logout_dialog_ok,
    )
}
