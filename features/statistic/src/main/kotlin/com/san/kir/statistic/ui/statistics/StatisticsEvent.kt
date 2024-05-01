package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StatisticsEvent : Action {
    data class Delete(val itemId: Long) : StatisticsEvent
}
