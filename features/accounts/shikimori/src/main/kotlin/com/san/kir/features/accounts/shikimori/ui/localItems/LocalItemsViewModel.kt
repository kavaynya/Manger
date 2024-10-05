package com.san.kir.features.accounts.shikimori.ui.localItems

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.features.accounts.shikimori.logic.Helper
import com.san.kir.features.accounts.shikimori.logic.HelperImpl
import com.san.kir.features.accounts.shikimori.logic.di.accountItemRepository
import com.san.kir.features.accounts.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.accounts.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.accounts.shikimori.logic.useCases.BindingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocalItemsViewModel(
    private val accountId: Long,
    private val libraryRepository: LibraryItemRepository = ManualDI.libraryItemRepository(),
    profileRepository: AccountItemRepository = ManualDI.accountItemRepository(accountId),
) : ViewModel<LocalItemsState>(), LocalItemsStateHolder, Helper<LibraryMangaItem> by HelperImpl() {

    private var job: Job? = null
    private val bindingHelper = BindingUseCase(profileRepository)

    init {
        sendAction(LocalItemsAction.Update)
    }

    override val tempState = combine(hasAction, unbindedItems, ::LocalItemsState)
    override val defaultState = LocalItemsState()

    override suspend fun onAction(action: Action) {
        when (action) {
            LocalItemsAction.Update -> updateItemsAndBinding()
        }
    }

    private fun updateItemsAndBinding() {
        job?.cancel()
        job = libraryRepository
            .loadItems()
            .mapLatest(bindingHelper.prepareData())
            .onEach(send(true))
            .flatMapLatest(bindingHelper.checkBinding())
            .onEach(send())
            .flowOn(defaultDispatcher)
            .launch()
    }
}

