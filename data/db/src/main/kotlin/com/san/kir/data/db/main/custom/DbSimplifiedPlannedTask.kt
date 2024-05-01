package com.san.kir.data.db.main.custom

import androidx.room.ColumnInfo
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

internal data class DbSimplifiedPlannedTask(
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "manga") val manga: String = "",
    @ColumnInfo(name = "group_name") val groupName: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "catalog") val catalog: String = "",
    @ColumnInfo(name = "type") val type: PlannedType = PlannedType.MANGA,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = false,
    @ColumnInfo(name = "period") val period: PlannedPeriod = PlannedPeriod.DAY,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: PlannedWeek = PlannedWeek.MONDAY,
    @ColumnInfo(name = "hour") val hour: Int = 0,
    @ColumnInfo(name = "minute") val minute: Int = 0,
)
