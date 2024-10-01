package com.san.kir.features.accounts.shikimori.ui.accountRate

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem

internal sealed interface AccountRateAction : Action {
    data object Add : AccountRateAction
    data object Remove : AccountRateAction
    data object Update : AccountRateAction
    data object TryAgainLastAction : AccountRateAction
    data class Change(val item: AccountMangaItem) : AccountRateAction
}
