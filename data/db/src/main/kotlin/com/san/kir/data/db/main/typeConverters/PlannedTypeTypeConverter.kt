package com.san.kir.data.db.main.typeConverters

import androidx.room.TypeConverter
import com.san.kir.data.models.utils.PlannedType

internal class PlannedTypeTypeConverter {
    @TypeConverter
    fun typeToInt(type: PlannedType): Int = type.order

    @TypeConverter
    fun intToType(type: Int): PlannedType = PlannedType.entries.first { it.order == type }
}
