package com.san.kir.features.accounts.shikimori.ui.localItem

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.features.accounts.shikimori.ui.syncManager.SyncDialogEvent

internal sealed interface LocalItemAction : Action {
    data class Update(val mangaId: Long? = null) : LocalItemAction
    data class Sync(val event: SyncDialogEvent) : LocalItemAction
}
