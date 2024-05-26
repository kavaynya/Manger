package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.StatisticsRepository
import com.san.kir.data.statisticsRepository
import kotlinx.coroutines.flow.combine

internal class StatisticsViewModel(
    private val statisticRepository: StatisticsRepository = ManualDI.statisticsRepository(),
) : ViewModel<StatisticsState>(), StatisticsStateHolder {

    override val tempState =
        combine(statisticRepository.simplifiedItems, statisticRepository.allTime) { items, time ->
            StatisticsState(items, time)
        }

    override val defaultState = StatisticsState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is StatisticsAction.Delete -> statisticRepository.delete(action.itemId)
        }
    }
}
