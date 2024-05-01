package com.san.kir.schedule.ui.task

import androidx.compose.runtime.Stable
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.db.main.entites.DbPlannedTask

@Stable
internal data class TaskState(
    val item: PlannedTask = PlannedTask(),
    val categoryName: String = "",
    val mangaName: String = "",
    val groupNames: List<String> = emptyList(),
    val categoryIds: List<Long> = emptyList(),
    val categoryNames: List<String> = emptyList(),
    val catalogNames: List<String> = emptyList(),
    val mangaIds: List<Long> = emptyList(),
    val mangaNames: List<String> = emptyList(),
    val availableAction: AvailableAction = AvailableAction.None,
) : ScreenState {
    companion object {
        val weeks = emptyList(*com.san.kir.data.models.utils.PlannedWeek.values())
        val periods = emptyList(*com.san.kir.data.models.utils.PlannedPeriod.values())
        val types = emptyList(*com.san.kir.data.models.utils.PlannedType.values())
    }
}

internal sealed interface AvailableAction {
    data object Save : AvailableAction
    data object Start : AvailableAction
    data object None : AvailableAction
}

