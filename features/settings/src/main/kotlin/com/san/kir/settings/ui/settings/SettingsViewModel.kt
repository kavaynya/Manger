package com.san.kir.settings.ui.settings

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.SettingsRepository
import com.san.kir.data.settingsRepository
import kotlinx.coroutines.flow.map

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository = ManualDI.settingsRepository(),
) : ViewModel<SettingsState>(), SettingsStateHolder {
    override val tempState = settingsRepository.settings.map {
        SettingsState(it.main, it.download, it.viewer, it.chapters)
    }

    override val defaultState = SettingsState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is SettingsAction.SaveChapters -> settingsRepository.update(action.state)
            is SettingsAction.SaveDownload -> settingsRepository.update(action.state)
            is SettingsAction.SaveMain -> settingsRepository.update(action.state)
            is SettingsAction.SaveViewer -> settingsRepository.update(action.state)
        }
    }
}
