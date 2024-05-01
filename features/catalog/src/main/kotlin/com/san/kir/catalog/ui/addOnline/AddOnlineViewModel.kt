package com.san.kir.catalog.ui.addOnline

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

internal class AddOnlineViewModel(
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager,
) : ViewModel<AddOnlineState>(), AddOnlineStateHolder {
    private val siteNames: List<String> = manager.catalog.map { it.catalogName }
    private var job: Job? = null

    private val isCheckingUrlState = MutableStateFlow(false)
    private val validatesCatalogsState = MutableStateFlow(siteNames)
    private val isErrorAvailableState = MutableStateFlow(false)
    private val isEnableAddingState = MutableStateFlow(false)

    override val tempState = combine(
        isCheckingUrlState,
        validatesCatalogsState,
        isErrorAvailableState,
        isEnableAddingState
    ) { check, validate, error, add ->
        AddOnlineState(check, validate, error, add)
    }

    override val defaultState = AddOnlineState(
        validatesCatalogs = siteNames,
    )

    override suspend fun onEvent(event: Action) {
        when (event) {
            is AddOnlineEvent.Update -> {
                checkUrl(event.text)
                isErrorAvailableState.value = false
            }
        }
    }

    private fun checkUrl(text: String) {
        job?.cancel()
        isCheckingUrlState.value = false
        job = viewModelScope.defaultLaunch {

            if (text.isNotBlank()) {
                // список сайтов подходящий под введеный адрес
                val temp = siteNames
                    .filter { it.contains(text) }

                // Если список не пуст, то отображаем его
                if (temp.isNotEmpty()) {
                    isEnableAddingState.value = false
                    validatesCatalogsState.update { temp }
                } else {
                    // Если в списке что-то есть
                    // то получаем соответствующий сайт
                    val site = siteNames
                        .filter { text.contains(it) }

                    //Если есть хоть один сайт, то проверяем валидность
                    if (site.isNotEmpty()) {
                        validatesCatalogsState.update { site }

                        isCheckingUrlState.value = true
                        delay(3.seconds)
                        isErrorAvailableState.value = manager.elementByUrl(text) == null
                        isEnableAddingState.value = isErrorAvailableState.value.not()
                        isCheckingUrlState.value = false
                    } else {
                        isEnableAddingState.value = false
                        validatesCatalogsState.update { emptyList() }
                    }
                }
            }
            // Если нет текста, то отображается список
            // доступных сайтов
            else {
                isEnableAddingState.value = false
                validatesCatalogsState.update { siteNames }
            }
        }
    }
}
