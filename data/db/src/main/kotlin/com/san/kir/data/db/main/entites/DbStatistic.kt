package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistic")
internal data class DbStatistic(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("manga_id") val mangaId: Long = 0L,
    @ColumnInfo("all_chapters") val allChapters: Int = 0,
    @ColumnInfo("last_chapters") val lastChapters: Int = 0,
    @ColumnInfo("all_pages") val allPages: Int = 0,
    @ColumnInfo("last_pages") val lastPages: Int = 0,
    @ColumnInfo("all_time") val allTime: Long = 0L,
    @ColumnInfo("last_time") val lastTime: Long = 0L,
    @ColumnInfo("max_speed") val maxSpeed: Int = 0,
    @ColumnInfo("download_size") val downloadSize: Long = 0L,
    @ColumnInfo("last_download_size") val lastDownloadSize: Long = 0L,
    @ColumnInfo("download_time") val downloadTime: Long = 0L,
    @ColumnInfo("last_download_time") val lastDownloadTime: Long = 0L,
    @ColumnInfo("opened_times") val openedTimes: Int = 0,
)
