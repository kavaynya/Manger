package com.san.kir.schedule.ui.updates

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.mangaRepository
import kotlinx.coroutines.flow.map

internal class UpdatesViewModel(
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
) : ViewModel<UpdatesState>(), UpdatesStateHolder {
    override val tempState = mangaRepository.miniItems.map { UpdatesState(it) }
    override val defaultState = UpdatesState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is UpdatesAction.Update ->
                mangaRepository.changeIsUpdate(action.itemId, action.updateState)
        }
    }
}
