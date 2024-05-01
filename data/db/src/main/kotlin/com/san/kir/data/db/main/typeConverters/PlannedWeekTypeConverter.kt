package com.san.kir.data.db.main.typeConverters

import androidx.room.TypeConverter
import com.san.kir.data.models.utils.PlannedWeek

internal class PlannedWeekTypeConverter {
    @TypeConverter
    fun typeToInt(type: PlannedWeek): Int = type.order

    @TypeConverter
    fun intToType(type: Int): PlannedWeek = PlannedWeek.entries.first { it.order == type }
}
