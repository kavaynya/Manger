package com.san.kir.data.db.main.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.san.kir.data.models.utils.DownloadState

@DatabaseView(
    viewName = "simple_chapter",
    value = "SELECT " +
            "chapters.id, " +
            "chapters.status, " +
            "chapters.progress, " +
            "chapters.isRead, " +
            "chapters.downloadPages, " +
            "chapters.pages, " +
            "chapters.name, " +
            "manga.name AS manga, " +
            "chapters.date, " +
            "chapters.path, " +
            "chapters.added_timestamp " +
            "FROM chapters JOIN manga ON chapters.manga_id=manga.id " +
            "WHERE chapters.isInUpdate IS 1 ORDER BY chapters.id DESC"
)
internal data class ViewChapter(
    @ColumnInfo("id") val id: Long = 0,
    @ColumnInfo("status") val status: DownloadState,
    @ColumnInfo("progress") val progress: Int,
    @ColumnInfo("isRead") val isRead: Boolean,
    @ColumnInfo("downloadPages") val downloadPages: Int,
    @ColumnInfo("pages") val pages: List<String>,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("manga") val manga: String,
    @ColumnInfo("date") val date: String,
    @ColumnInfo("path") val path: String,
    @ColumnInfo("added_timestamp") val addedTimestamp: Long
)
