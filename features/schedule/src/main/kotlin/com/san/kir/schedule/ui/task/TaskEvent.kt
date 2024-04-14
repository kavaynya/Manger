package com.san.kir.schedule.ui.task

import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek
import com.san.kir.core.utils.viewModel.ScreenEvent

internal sealed interface TaskEvent : ScreenEvent {
    data class Set(val itemId: Long) : TaskEvent
    data class Change(val type: ChangeType) : TaskEvent
    data object Save : TaskEvent
    data object Start : TaskEvent
}

internal sealed interface ChangeType {
    data class Type(val type: com.san.kir.data.models.utils.PlannedType) : ChangeType
    data class Manga(val mangaId: Long) : ChangeType
    data class Group(val name: String) : ChangeType
    data class Mangas(val mangaIds: List<Long>) : ChangeType
    data class Category(val categoryId: Long) : ChangeType
    data class Catalog(val name: String) : ChangeType
    data class Period(val period: com.san.kir.data.models.utils.PlannedPeriod) : ChangeType
    data class Day(val day: com.san.kir.data.models.utils.PlannedWeek) : ChangeType
    data class Time(val hour: Int, val minute: Int) : ChangeType
}
