package com.san.kir.schedule.ui.task

import androidx.compose.runtime.Stable
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.PlannedTask
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

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
    val backgroundWork: BackgroundWork = BackgroundWork()
) : ScreenState {
    companion object {
        val weeks = PlannedWeek.entries
        val periods = PlannedPeriod.entries
        val types = PlannedType.entries
    }
}

internal sealed interface AvailableAction {
    data object Save : AvailableAction
    data object Start : AvailableAction
    data object None : AvailableAction
}

internal data class BackgroundWork(val hasWork: Boolean = false, val hasAction: Boolean = false) {
    val hasBackgrounds = hasWork || hasAction
}
