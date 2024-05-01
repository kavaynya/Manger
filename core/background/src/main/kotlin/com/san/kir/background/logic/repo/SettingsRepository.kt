package com.san.kir.background.logic.repo

import com.san.kir.data.db.main.dao.SettingsDao
import com.san.kir.data.db.main.repo.AbstractSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepository (
    settingsDao: SettingsDao,
) : AbstractSettingsRepository(settingsDao) {

    suspend fun currentDownload() = settings().map { it.download }.first()
}
