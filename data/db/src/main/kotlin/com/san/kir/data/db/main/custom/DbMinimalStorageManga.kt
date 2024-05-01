package com.san.kir.data.db.main.custom

import androidx.room.ColumnInfo

internal data class DbMinimalStorageManga(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "logo") val logo: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "path") val path: String,
)
