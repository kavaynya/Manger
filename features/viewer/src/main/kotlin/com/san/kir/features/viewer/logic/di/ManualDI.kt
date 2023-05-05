package com.san.kir.features.viewer.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterDao
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.settingsDao
import com.san.kir.data.statisticDao
import com.san.kir.features.viewer.logic.ChaptersManager
import com.san.kir.features.viewer.logic.SettingsRepository

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)

internal val ManualDI.chaptersManager: ChaptersManager
    get() = ChaptersManager(chapterDao, statisticDao, siteCatalogsManager)
