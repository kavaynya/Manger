package com.san.kir.schedule.ui.task

import com.san.kir.core.utils.viewModel.Action
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

internal sealed interface TaskAction : Action {
    data class Change(val type: ChangeType) : TaskAction
    data object Save : TaskAction
    data object Start : TaskAction
}

internal sealed interface ChangeType {
    @JvmInline
    value class Type(val type: PlannedType) : ChangeType
    @JvmInline
    value class Manga(val mangaId: Long) : ChangeType
    @JvmInline
    value class Group(val name: String) : ChangeType
    @JvmInline
    value class Mangas(val mangaIds: List<Long>) : ChangeType
    @JvmInline
    value class Category(val categoryId: Long) : ChangeType
    @JvmInline
    value class Catalog(val name: String) : ChangeType
    @JvmInline
    value class Period(val period: PlannedPeriod) : ChangeType
    @JvmInline
    value class Day(val day: PlannedWeek) : ChangeType
    data class Time(val hour: Int, val minute: Int) : ChangeType
}
