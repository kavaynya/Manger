package com.san.kir.statistic.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.mangaDao
import com.san.kir.data.statisticDao
import com.san.kir.statistic.logic.repo.StatisticRepository

internal val ManualDI.statisticRepository: StatisticRepository
    get() = StatisticRepository(statisticDao, mangaDao)
