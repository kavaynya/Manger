package com.san.kir.statistic.ui.statistic

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.Statistic
import com.san.kir.statistic.logic.di.statisticRepository
import com.san.kir.statistic.logic.repo.StatisticRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

internal class StatisticViewModel(
    private val statisticRepository: StatisticRepository = ManualDI.statisticRepository,
) : ViewModel<StatisticState>(), StatisticStateHolder {
    private val statistic = MutableStateFlow(Statistic())
    private val mangaName = MutableStateFlow("")

    override val tempState = combine(statistic, mangaName, ::StatisticState)
    override val defaultState = StatisticState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is StatisticEvent.Set -> set(event.itemId)
        }
    }

    private suspend fun set(itemId: Long) {
        val item = statisticRepository.item(itemId)
        Timber.v(item.toString())
        statistic.value = item
        mangaName.value = statisticRepository.mangaName(statistic.value.mangaId).name
    }
}
