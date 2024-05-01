package com.san.kir.data.db.workers.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.DownloadState

@Entity(tableName = "catalog_task")
internal data class DbCatalogTask(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "state") val state: DownloadState = DownloadState.QUEUED,
    @ColumnInfo(name = "progress") val progress: Float = 0f,
)
