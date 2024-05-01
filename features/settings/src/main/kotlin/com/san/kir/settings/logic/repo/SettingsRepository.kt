package com.san.kir.settings.logic.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.SettingsDao
import com.san.kir.data.db.main.repo.AbstractSettingsRepository
import com.san.kir.data.db.main.entites.DbSettings

internal class SettingsRepository(
    settingsDao: SettingsDao
) : AbstractSettingsRepository(settingsDao) {

    suspend fun update(state: Settings.Chapters) = withIoContext {
        settingsDao.update(currentSettings().copy(chapters = state))
    }

    suspend fun update(state: Settings.Download) = withIoContext {
        settingsDao.update(currentSettings().copy(download = state))
    }

    suspend fun update(state: Settings.Main) = withIoContext {
        settingsDao.update(currentSettings().copy(main = state))
    }

    suspend fun update(state: Settings.Viewer) = withIoContext {
        settingsDao.update(currentSettings().copy(viewer = state))
    }
}
