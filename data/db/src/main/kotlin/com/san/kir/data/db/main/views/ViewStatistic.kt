package com.san.kir.data.db.main.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    viewName = "simple_statistic",
    value = "SELECT statistic.id AS id, " +
            "manga.name AS manga_name, " +
            "manga.logo AS manga_logo, " +
            "statistic.all_time AS all_time " +
            "FROM statistic JOIN manga ON statistic.manga_id=manga.id " +
            "ORDER BY all_time DESC"
)
internal data class ViewStatistic(
    @ColumnInfo("id") val id: Long = 0,
    @ColumnInfo("manga_name") val name: String = "",
    @ColumnInfo("manga_logo") val logo: String = "",
    @ColumnInfo("all_time") val allTime: Long = 0
)
