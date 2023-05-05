package com.san.kir.manger.logic.repo

import com.san.kir.data.db.dao.SettingsDao
import com.san.kir.data.db.repositories.AbstractSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class SettingsRepository constructor(
    settingsDao: SettingsDao
) : AbstractSettingsRepository(settingsDao) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun main() = settings().mapLatest { it.main }
}
