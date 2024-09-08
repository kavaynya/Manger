package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbSettings
import com.san.kir.data.models.main.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


internal fun DbSettings.toModel() =
    Settings(
        isFirstLaunch,
        Settings.Chapters(isIndividual, isTitle, filterStatus),
        Settings.Download(concurrent, retry, wifi),
        Settings.Main(isDarkTheme, isShowCategory, editMenu),
        Settings.Viewer(
            orientation,
            cutOut,
            Settings.Viewer.Control(taps, swipes, keys),
            withoutSaveFiles,
            useScrollbars
        )
    )

@JvmName("toFlowSettingsModel")
internal fun Flow<DbSettings>.toModel() = map { it.toModel() }
