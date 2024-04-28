package com.san.kir.schedule.ui.task

import androidx.compose.runtime.Stable
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.base.PlannedTask
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
internal data class TaskState(
    val item: PlannedTask = PlannedTask(),
    val categoryName: String = "",
    val mangaName: String = "",
    val groupNames: ImmutableList<String> = persistentListOf(),
    val categoryIds: ImmutableList<Long> = persistentListOf(),
    val categoryNames: ImmutableList<String> = persistentListOf(),
    val catalogNames: ImmutableList<String> = persistentListOf(),
    val mangaIds: ImmutableList<Long> = persistentListOf(),
    val mangaNames: ImmutableList<String> = persistentListOf(),
    val availableAction: AvailableAction = AvailableAction.None,
) : ScreenState {
    companion object {
        val weeks = persistentListOf(*com.san.kir.data.models.utils.PlannedWeek.values())
        val periods = persistentListOf(*com.san.kir.data.models.utils.PlannedPeriod.values())
        val types = persistentListOf(*com.san.kir.data.models.utils.PlannedType.values())
    }
}

internal sealed interface AvailableAction {
    data object Save : AvailableAction
    data object Start : AvailableAction
    data object None : AvailableAction
}

