package com.san.kir.data.db.main.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    viewName = "libarary_manga",
    value = "WITH all_count AS (SELECT manga_id AS id, COUNT(manga_id) AS count FROM chapters GROUP BY manga_id), " +
            "read_count AS (SELECT manga_id AS id, COUNT(manga_id) AS count FROM chapters WHERE isRead IS 1 GROUP BY manga_id) " +
            "SELECT " +
            "manga.id, " +
            "manga.name, " +
            "manga.logo, " +
            "manga.about, " +
            "manga.isAlternativeSort, " +
            "read_count.count AS read_chapters, " +
            "all_count.count AS all_chapters " +
            "FROM manga JOIN all_count ON manga.id = all_count.id JOIN read_count ON manga.id = read_count.id"
)
internal data class ViewMangaWithChapterCounts(
    @ColumnInfo("id") val id: Long = 0,
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("logo") val logo: String = "",
    @ColumnInfo("about") val description: String = "",
    @ColumnInfo("isAlternativeSort") val sort: Boolean = false,
    @ColumnInfo("read_chapters") val read: Int = 0,
    @ColumnInfo("all_chapters") val all: Int = 0,
)
