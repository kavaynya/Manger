package com.san.kir.data.db.main.custom

import androidx.room.ColumnInfo

internal data class DbMinimalTaskManga(
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "isUpdate") val update: Boolean = false,
    @ColumnInfo(name = "category") val category: String = "",
)
