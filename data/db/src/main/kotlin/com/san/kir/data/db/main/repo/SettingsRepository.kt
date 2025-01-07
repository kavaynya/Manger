package com.san.kir.data.db.main.repo

import com.san.kir.data.db.main.dao.SettingsDao
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.models.main.Settings
import com.san.kir.data.models.utils.ChapterFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


public class SettingsRepository internal constructor(private val settingsDao: SettingsDao) {
    private val _settings = settingsDao.loadItem().filterNotNull()
    public val settings: Flow<Settings> = _settings.toModel().distinctUntilChanged()
    public val isDarkTheme: Flow<Boolean> = _settings.map { it.isDarkTheme }.distinctUntilChanged()
    public val wifi: Flow<Boolean> = _settings.map { it.wifi }.distinctUntilChanged()
    public val showTitle: Flow<Boolean> = _settings.map { it.isTitle }.distinctUntilChanged()
    public val isIndividual: Flow<Boolean> = _settings.map { it.isIndividual }.distinctUntilChanged()
    public val isShowCategory: Flow<Boolean> = _settings.map { it.isShowCategory }.distinctUntilChanged()
    public val useScrollbars: Flow<Boolean> = _settings.map { it.useScrollbars }.distinctUntilChanged()
    public val viewer: Flow<Settings.Viewer> = settings.map { it.viewer }.distinctUntilChanged()
    public val control: Flow<Settings.Viewer.Control> = viewer.map { it.control }.distinctUntilChanged()

    private suspend fun currentSettings() = _settings.first()
    public suspend fun isIndividual(): Boolean = currentSettings().isIndividual
    public suspend fun filterStatus(): ChapterFilter = currentSettings().filterStatus
    public suspend fun withoutSaveFiles(): Boolean = currentSettings().withoutSaveFiles
    public suspend fun concurrent(): Boolean = currentSettings().concurrent
    public suspend fun retry(): Boolean = currentSettings().retry
    public suspend fun wifi(): Boolean = currentSettings().wifi

    public suspend fun update(newFilter: ChapterFilter): Unit =
        settingsDao.update(currentSettings().copy(filterStatus = newFilter))

    public suspend fun update(state: Settings.Chapters): Unit = settingsDao.update(
        currentSettings().copy(
            isIndividual = state.isIndividual,
            isTitle = state.isTitle,
            filterStatus = state.filterStatus
        )
    )

    public suspend fun update(state: Settings.Download): Unit = settingsDao.update(
        currentSettings().copy(
            concurrent = state.concurrent,
            retry = state.retry,
            wifi = state.wifi
        )
    )

    public suspend fun update(state: Settings.Main): Unit = settingsDao.update(
        currentSettings().copy(
            isDarkTheme = state.isDarkTheme,
            isShowCategory = state.isShowCategory,
            editMenu = state.editMenu
        )
    )

    public suspend fun update(state: Settings.Viewer): Unit = settingsDao.update(
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
