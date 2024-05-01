package com.san.kir.data.db.workers.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.DownloadState

@Entity(tableName = "manga_task")
data class DbMangaTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "manga_id") val mangaId: Long = 0L,
    @ColumnInfo(name = "manga_name") val mangaName: String = "",
    @ColumnInfo(name = "new_chapters") val newChapters: Int = 0,
    @ColumnInfo(name = "state") val state: DownloadState = DownloadState.QUEUED,
)
