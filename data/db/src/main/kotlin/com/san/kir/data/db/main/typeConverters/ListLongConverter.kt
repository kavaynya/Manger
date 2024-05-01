package com.san.kir.data.db.main.typeConverters

import androidx.room.TypeConverter

internal class ListLongConverter {
    @TypeConverter
    fun listToString(list: List<Long>): String = list.joinToString(",")

    @TypeConverter
    fun stringToList(string: String): List<Long> {
        if (string.isEmpty()) return emptyList()
        return string.split(",").map { it.removeSurrounding(" ", " ").toLong() }
    }
}
