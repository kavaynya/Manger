package com.san.kir.features.accounts.shikimori.ui.syncManager

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem

internal sealed interface SyncManagerAction : Action {
    data class ApplySync(val itemId: Long) : SyncManagerAction
    data class Update(val item: SyncItemState) : SyncManagerAction
    data class Hide(val itemId: Long) : SyncManagerAction
    data class CancelSync(val item: MangaItem) : SyncManagerAction
}
