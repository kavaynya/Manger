package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.views.ViewStatistic

internal data class StatisticsState(
    val items: List<SimplifiedStatistic> = emptyList(),
    val allTime: Long = 0,
) : ScreenState
