package com.san.kir.features.shikimori.ui.localItems

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultDispatcher
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.extend.SimplifiedMangaWithChapterCounts
import com.san.kir.features.shikimori.logic.Helper
import com.san.kir.features.shikimori.logic.HelperImpl
import com.san.kir.features.shikimori.logic.di.libraryItemRepository
import com.san.kir.features.shikimori.logic.di.profileItemRepository
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import com.san.kir.features.shikimori.logic.useCases.BindingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
internal class LocalItemsViewModel(
    private val libraryRepository: LibraryItemRepository = ManualDI.libraryItemRepository,
    profileRepository: ProfileItemRepository = ManualDI.profileItemRepository,
) : ViewModel<LocalItemsState>(), LocalItemsStateHolder,
    Helper<SimplifiedMangaWithChapterCounts> by HelperImpl() {

    private var job: Job? = null
    private val bindingHelper = BindingUseCase(profileRepository)

    init {
        sendAction(LocalItemsEvent.Update)
    }

    override val tempState = combine(hasAction, unbindedItems, ::LocalItemsState)
    override val defaultState = LocalItemsState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            LocalItemsEvent.Update -> updateItemsAndBinding()
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
            .launchIn(viewModelScope)
    }
}

