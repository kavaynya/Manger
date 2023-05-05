package com.san.kir.settings.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.settingsDao
import com.san.kir.settings.logic.repo.SettingsRepository

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)
