package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.ChapterFilter

@Entity(tableName = "manga")
internal data class DbManga(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("host") val host: String = "",
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("logo") val logo: String = "",
    @ColumnInfo("about") val about: String = "",
    @ColumnInfo("category_id", defaultValue = "0") val categoryId: Long = 0,
    @ColumnInfo("path") val path: String = "",
    @ColumnInfo("status") val status: String = "",
    @ColumnInfo("color") val color: Int = 0,
    @ColumnInfo("populate") val populate: Int = 0,
    @ColumnInfo("ordering") val order: Int = 0,
    @ColumnInfo("isAlternativeSort") val isAlternativeSort: Boolean = true,
    @ColumnInfo("isUpdate") val isUpdate: Boolean = true,
    @ColumnInfo("chapterFilter") val chapterFilter: ChapterFilter = ChapterFilter.ALL_READ_ASC,
    @ColumnInfo("isAlternativeSite") val isAlternativeSite: Boolean = false,
    @ColumnInfo("shortLink") val shortLink: String = "",
    @ColumnInfo("authors") val authorsList: List<String> = listOf(),
    @ColumnInfo("genres") val genresList: List<String> = listOf(),
    @ColumnInfo("lastUpdateError", defaultValue = "") val lastUpdateError: String = ""
)
