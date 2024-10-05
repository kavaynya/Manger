package com.san.kir.features.accounts.shikimori.ui.accountScreen

import com.san.kir.core.utils.viewModel.StateHolder
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.useCases.BindStatus
import kotlinx.coroutines.flow.StateFlow

internal interface AccountStateHolder : StateHolder<AccountState> {
    val boundedItems: StateFlow<List<AccountMangaItem>>
    val unboundedItems: StateFlow<List<BindStatus<AccountMangaItem>>>
}
