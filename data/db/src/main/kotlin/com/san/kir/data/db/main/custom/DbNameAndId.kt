package com.san.kir.data.db.main.custom

import androidx.room.ColumnInfo

internal data class DbNameAndId(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
)
