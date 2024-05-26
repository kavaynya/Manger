package com.san.kir.statistic.ui.statistics

import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.utils.viewModel.Event

internal interface StatisticsEvent : Event {
    data class ToStatistic(val id: Long, val params: SharedParams) : StatisticsEvent
}
