package com.san.kir.features.viewer.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterRepository
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.statisticsRepository
import com.san.kir.features.viewer.logic.ChaptersManager

internal val ManualDI.chaptersManager: ChaptersManager
    get() = ChaptersManager(chapterRepository(), statisticsRepository(), siteCatalogsManager())
