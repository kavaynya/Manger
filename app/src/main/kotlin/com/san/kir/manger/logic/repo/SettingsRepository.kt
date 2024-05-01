package com.san.kir.manger.logic.repo

import com.san.kir.data.db.main.dao.SettingsDao
import com.san.kir.data.db.main.repo.AbstractSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class SettingsRepository(
    settingsDao: SettingsDao
) : AbstractSettingsRepository(settingsDao) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun main() = settings().mapLatest { it.main }
}
