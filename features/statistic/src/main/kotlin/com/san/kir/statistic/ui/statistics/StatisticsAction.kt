package com.san.kir.statistic.ui.statistics

import com.san.kir.core.utils.viewModel.Action

internal sealed interface StatisticsAction : Action {
    data class Delete(val itemId: Long) : StatisticsAction
}
