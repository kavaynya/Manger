package com.san.kir.manger.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.lazyPlannedDao
import com.san.kir.data.settingsDao
import com.san.kir.manger.logic.repo.InitRepository
import com.san.kir.manger.logic.repo.SettingsRepository

val ManualDI.initRepository: InitRepository
    get() = InitRepository(context, lazyPlannedDao)

val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)
