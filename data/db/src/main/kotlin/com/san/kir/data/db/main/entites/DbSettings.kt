package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.ChapterFilter
import com.san.kir.data.models.utils.Orientation

@Entity(tableName = "settings")
internal data class DbSettings(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = false) val id: Long = 1,
    @ColumnInfo("isFirstLaunch") val isFirstLaunch: Boolean = true,
    @ColumnInfo("isIndividual") val isIndividual: Boolean = true,
    @ColumnInfo("isTitle") val isTitle: Boolean = true,
    @ColumnInfo("filterStatus") val filterStatus: ChapterFilter = ChapterFilter.ALL_READ_ASC,
    @ColumnInfo("concurrent") val concurrent: Boolean = true,
    @ColumnInfo("retry") val retry: Boolean = false,
    @ColumnInfo("wifi") val wifi: Boolean = false,
    @ColumnInfo("theme") val isDarkTheme: Boolean = true,
    @ColumnInfo("isShowCategory") val isShowCategory: Boolean = true,
    @ColumnInfo("editMenu") val editMenu: Boolean = false,
    @ColumnInfo("orientation") val orientation: Orientation = Orientation.AUTO_LAND,
    @ColumnInfo("cutOut") val cutOut: Boolean = true,
    @ColumnInfo("taps") val taps: Boolean = false,
    @ColumnInfo("swipes") val swipes: Boolean = true,
    @ColumnInfo("keys") val keys: Boolean = false,
    @ColumnInfo("withoutSaveFiles") val withoutSaveFiles: Boolean = false,
    @ColumnInfo("scrollbars") val useScrollbars: Boolean = true,
)
