package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.SettingsDao
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.models.main.Settings
import com.san.kir.data.models.utils.ChapterFilter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class SettingsRepository internal constructor(private val settingsDao: SettingsDao) {
    private val _settings = settingsDao.loadItem().filterNotNull()
    val settings = _settings.toModel()
    val theme = _settings.map { it.theme }
    val wifi = _settings.map { it.wifi }
    val showTitle = _settings.map { it.isTitle }
    val isIndividual = _settings.map { it.isIndividual }
    val isShowCategory = _settings.map { it.isShowCategory }
    val useScrollbars = _settings.map { it.useScrollbars }
    val viewer = settings.map { it.viewer }
    val control = viewer.map { it.control }

    private suspend fun currentSettings() = _settings.first()
    suspend fun isIndividual() = currentSettings().isIndividual
    suspend fun filterStatus() = currentSettings().filterStatus
    suspend fun withoutSaveFiles() = currentSettings().withoutSaveFiles
    suspend fun concurrent() = currentSettings().concurrent
    suspend fun retry() = currentSettings().retry
    suspend fun wifi() = currentSettings().wifi

    suspend fun update(newFilter: ChapterFilter) =
        withIoContext { settingsDao.update(currentSettings().copy(filterStatus = newFilter)) }

    suspend fun update(state: Settings.Chapters) = withIoContext {
        settingsDao.update(
            currentSettings().copy(
                isIndividual = state.isIndividual,
                isTitle = state.isTitle,
                filterStatus = state.filterStatus
            )
        )
    }

    suspend fun update(state: Settings.Download) = withIoContext {
        settingsDao.update(
            currentSettings().copy(
                concurrent = state.concurrent,
                retry = state.retry,
                wifi = state.wifi
            )
        )
    }

    suspend fun update(state: Settings.Main) = withIoContext {
        settingsDao.update(
            currentSettings().copy(
                theme = state.theme,
                isShowCategory = state.isShowCategory,
                editMenu = state.editMenu
            )
        )
    }

    suspend fun update(state: Settings.Viewer) = withIoContext {
        settingsDao.update(
            currentSettings().copy(
                orientation = state.orientation,
                cutOut = state.cutOut,
                withoutSaveFiles = state.withoutSaveFiles,
                useScrollbars = state.useScrollbars,
                taps = state.control.taps,
                swipes = state.control.swipes,
                keys = state.control.keys,
            )
        )
    }
}
