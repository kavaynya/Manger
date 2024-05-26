package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.SimplifiedStatistic

internal data class StatisticsState(
    val items: List<SimplifiedStatistic> = emptyList(),
    val allTime: Long = 0,
    val allReadingItems: Int = items.count { it.allTime > 0 },
) : ScreenState
