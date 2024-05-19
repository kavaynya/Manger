package com.san.kir.settings.ui.settings

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.main.Settings


internal sealed interface SettingsAction : Action {
    data class SaveMain(val state: Settings.Main) : SettingsAction
    data class SaveDownload(val state: Settings.Download) : SettingsAction
    data class SaveViewer(val state: Settings.Viewer) : SettingsAction
    data class SaveChapters(val state: Settings.Chapters) : SettingsAction
}
