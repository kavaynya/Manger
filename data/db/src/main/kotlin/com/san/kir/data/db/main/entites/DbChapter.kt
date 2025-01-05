package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.DownloadState

@Entity(tableName = "chapters")
internal data class DbChapter(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("manga_id", defaultValue = "0") val mangaId: Long = 0,
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("date") val date: String = "",
    @ColumnInfo("path") val path: String = "",
    @ColumnInfo("isRead") val isRead: Boolean = false,
    @ColumnInfo("site") val link: String = "",
    @ColumnInfo("progress") val progress: Int = 0,
    @ColumnInfo("pages") val pages: List<String> = listOf(),
    @ColumnInfo("isInUpdate") val isInUpdate: Boolean = false, // Пометка, что глав отображается в обновлениях
    @ColumnInfo("downloadPages") val downloadPages: Int = 0,
    @ColumnInfo("downloadSize") val downloadSize: Long = 0L,
    @ColumnInfo("totalTime") val downloadTime: Long = 0L,
    @ColumnInfo("status") val status: DownloadState = DownloadState.UNKNOWN,
    @ColumnInfo("ordering") val order: Long = 0,
    @ColumnInfo("added_timestamp", defaultValue = "0") val addedTimestamp: Long = System.currentTimeMillis(),
)
