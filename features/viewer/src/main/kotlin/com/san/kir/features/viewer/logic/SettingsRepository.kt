package com.san.kir.features.viewer.logic

import com.san.kir.data.db.dao.SettingsDao
import com.san.kir.data.db.repositories.AbstractSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
internal class SettingsRepository(
    settingsDao: SettingsDao,
) : AbstractSettingsRepository(settingsDao) {

    fun viewer() = settings().mapLatest { it.viewer }

    suspend fun currentViewer() = currentSettings().viewer
}
