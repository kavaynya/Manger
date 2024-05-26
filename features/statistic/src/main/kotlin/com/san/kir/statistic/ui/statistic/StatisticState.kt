package com.san.kir.statistic.ui.statistic

import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.Statistic

internal data class StatisticState(
    val item: Statistic = Statistic(),
    val mangaName: String = "",
) : ScreenState
