package com.san.kir.data.db.main.typeConverters

import androidx.room.TypeConverter
import com.san.kir.data.models.utils.PlannedPeriod

internal class PlannedPeriodTypeConverter {
    @TypeConverter
    fun typeToInt(type: PlannedPeriod): Int = type.order

    @TypeConverter
    fun intToType(type: Int): PlannedPeriod = PlannedPeriod.entries.first { it.order == type }
}
