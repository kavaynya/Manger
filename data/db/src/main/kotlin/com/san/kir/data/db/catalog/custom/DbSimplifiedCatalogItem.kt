package com.san.kir.data.db.catalog.custom

import androidx.room.ColumnInfo

internal data class DbSimplifiedCatalogItem(
    @ColumnInfo("id") val id: Long,
    @ColumnInfo("catalogName") val catalogName: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("statusEdition") val statusEdition: String,
    @ColumnInfo("shotLink") val shortLink: String,
    @ColumnInfo("link") val link: String,
    @ColumnInfo("genres") val genres: List<String>,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("authors") val authors: List<String>,
    @ColumnInfo("dateId") val dateId: Int,
    @ColumnInfo("populate") val populate: Int
)
