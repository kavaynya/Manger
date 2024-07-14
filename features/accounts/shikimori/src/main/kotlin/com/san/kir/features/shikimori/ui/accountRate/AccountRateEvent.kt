package com.san.kir.features.shikimori.ui.accountRate

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.base.ShikimoriRate
import com.san.kir.features.shikimori.logic.SyncDialogEvent

internal sealed interface AccountRateEvent : Action {
    data object ExistToggle : AccountRateEvent
    data class Update(val item: ShikimoriRate? = null, val id: Long? = null) : AccountRateEvent
    data class Sync(val event: SyncDialogEvent) : AccountRateEvent
}
