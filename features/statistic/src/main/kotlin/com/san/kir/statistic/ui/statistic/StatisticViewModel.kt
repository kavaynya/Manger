package com.san.kir.statistic.ui.statistic

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.StatisticsRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Statistic
import com.san.kir.data.statisticsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull

internal class StatisticViewModel(
    itemId: Long = -1,
    mangaId: Long = -1,
    statisticRepository: StatisticsRepository = ManualDI.statisticsRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
) : ViewModel<StatisticState>(), StatisticStateHolder {

    private val statistic = statisticRepository
        .item(itemId, mangaId)
        .filterNotNull()
        .stateInSubscribed(Statistic())

    private val mangaName = statistic
        .filterNotNull()
        .mapNotNull { mangaRepository.name(it.mangaId) }
        .stateInSubscribed("")

    override val tempState = combine(statistic, mangaName, ::StatisticState)
    override val defaultState = StatisticState()
}
