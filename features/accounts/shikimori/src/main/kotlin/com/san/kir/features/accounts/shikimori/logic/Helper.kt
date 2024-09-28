package com.san.kir.features.accounts.shikimori.logic

import com.san.kir.features.accounts.shikimori.logic.models.MangaItem
import com.san.kir.features.accounts.shikimori.logic.useCases.BindStatus
import com.san.kir.features.accounts.shikimori.logic.useCases.CheckingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal interface Helper<T : MangaItem> {

    val unbindedItems: StateFlow<List<BindStatus<T>>>

    val hasAction: StateFlow<BackgroundTasks>

    fun send(checkingState: Boolean): (List<BindStatus<T>>) -> Unit

    fun send(): (CheckingStatus<T>) -> Unit

    fun updateLoading(loading: Boolean)

}

internal class HelperImpl<T : MangaItem> : Helper<T> {

    // Манга без привязки
    override val unbindedItems = MutableStateFlow(emptyList<BindStatus<T>>())

    // Индикация о выполнении фоновых операций
    override val hasAction = MutableStateFlow(BackgroundTasks())

    override fun send(checkingState: Boolean): (List<BindStatus<T>>) -> Unit = {
        unbindedItems.value = it
        hasAction.update { old -> old.copy(checkBind = checkingState) }
    }

    override fun send(): (CheckingStatus<T>) -> Unit = {
        it.items?.let { items -> unbindedItems.value = items }
        hasAction.update { old ->
            old.copy(checkBind = it.progress != null, progress = it.progress)
        }
    }

    override fun updateLoading(loading: Boolean) {
        hasAction.update { old -> old.copy(loading = loading) }
    }
}

internal data class BackgroundTasks(
    val loading: Boolean = true,
    val checkBind: Boolean = true,
    val progress: Float? = null
)
