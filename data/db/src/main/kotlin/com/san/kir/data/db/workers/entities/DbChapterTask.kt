package com.san.kir.data.db.workers.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.DownloadState

@Entity(tableName = "chapter_task")
internal data class DbChapterTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "chapter_id") val chapterId: Long = 0L,
    @ColumnInfo(name = "state") val state: DownloadState = DownloadState.QUEUED,
    val chapterName: String = "",
    val max: Int = 0,
    val progress: Int = 0,
    val size: Long = 0,
    val time: Long = 0,
)
