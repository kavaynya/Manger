package com.san.kir.settings.ui.settings

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.settings.logic.di.settingsRepository
import com.san.kir.settings.logic.repo.SettingsRepository
import kotlinx.coroutines.flow.map

internal class SettingsViewModel(
    private val settingsRepository: SettingsRepository = ManualDI.settingsRepository,
) : ViewModel<SettingsState>(), SettingsStateHolder {
    override val tempState = settingsRepository.settings().map {
        SettingsState(it.main, it.download, it.viewer, it.chapters)
    }

    override val defaultState = SettingsState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is SettingsEvent.SaveChapters -> settingsRepository.update(event.state)
            is SettingsEvent.SaveDownload -> settingsRepository.update(event.state)
            is SettingsEvent.SaveMain -> settingsRepository.update(event.state)
            is SettingsEvent.SaveViewer -> settingsRepository.update(event.state)
        }
    }
}
