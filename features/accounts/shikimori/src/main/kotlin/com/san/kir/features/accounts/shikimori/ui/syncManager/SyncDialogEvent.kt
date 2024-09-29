package com.san.kir.features.accounts.shikimori.ui.syncManager

import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriRate

internal sealed interface SyncDialogEvent {

    data class SyncNext(val onlineIsTruth: Boolean = false) : SyncDialogEvent
    data object DialogDismiss : SyncDialogEvent
    data class SyncCancel(val rate: ShikimoriRate) : SyncDialogEvent
}
