package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.statistic.logic.di.statisticRepository
import com.san.kir.statistic.logic.repo.StatisticRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.combine

internal class StatisticsViewModel(
    private val statisticRepository: StatisticRepository = ManualDI.statisticRepository,
) : ViewModel<StatisticsState>(), StatisticsStateHolder {

    override val tempState =
        combine(statisticRepository.items, statisticRepository.allTime) { items, time ->
            StatisticsState(items.toPersistentList(), time)
        }

    override val defaultState = StatisticsState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is StatisticsEvent.Delete -> statisticRepository.delete(event.itemId)
        }
    }
}
