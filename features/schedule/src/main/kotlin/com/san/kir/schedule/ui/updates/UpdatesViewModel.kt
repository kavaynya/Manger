package com.san.kir.schedule.ui.updates

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.schedule.logic.repo.UpdatesRepository
import com.san.kir.schedule.logic.repo.updatesRepository
import kotlinx.coroutines.flow.map

internal class UpdatesViewModel(
    private val updatesRepository: UpdatesRepository = ManualDI.updatesRepository,
) : ViewModel<UpdatesState>(), UpdatesStateHolder {
    override val tempState = updatesRepository.items.map { UpdatesState(it) }
    override val defaultState = UpdatesState(emptyList())

    override suspend fun onEvent(event: Action) {
        when (event) {
            is UpdatesEvent.Update -> updatesRepository.update(event.itemId, event.updateState)
        }
    }
}
