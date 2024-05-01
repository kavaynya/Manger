package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StorageItem")
internal data class DbStorage(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("path") val path: String = "",
    @ColumnInfo("sizeFull") val sizeFull: Double = 0.0,
    @ColumnInfo("sizeRead") val sizeRead: Double = 0.0,
    @ColumnInfo("isNew") val isNew: Boolean = false,
    @ColumnInfo("catalogName") val catalogName: String = "",
)
