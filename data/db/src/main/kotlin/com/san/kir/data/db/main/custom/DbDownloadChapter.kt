package com.san.kir.data.db.main.custom

import androidx.room.ColumnInfo
import com.san.kir.data.models.utils.DownloadState

internal data class DbDownloadChapter(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "manga") val manga: String,
    @ColumnInfo(name = "logo") val logo: String,
    @ColumnInfo(name = "status") val status: DownloadState,
    @ColumnInfo(name = "totalTime") val totalTime: Long,
    @ColumnInfo(name = "downloadSize") val downloadSize: Long,
    @ColumnInfo(name = "downloadPages") val downloadPages: Int,
    @ColumnInfo(name = "pages") val pages: List<String>,
)
