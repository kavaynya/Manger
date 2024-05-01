package com.san.kir.statistic.ui.statistic

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StatisticEvent : Action {
    data class Set(val itemId: Long) : StatisticEvent
}
