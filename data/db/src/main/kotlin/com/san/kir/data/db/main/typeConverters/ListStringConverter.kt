package com.san.kir.data.db.main.typeConverters

import androidx.room.TypeConverter

internal class ListStringConverter {
    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString()

    @TypeConverter
    fun stringToList(string: String): List<String> {
        if (string.isEmpty()) return emptyList()
        return string.split(",").map { it.removeSurrounding(" ", " ") }
    }
}

