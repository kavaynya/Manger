package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.SortLibraryUtil

@Entity(tableName = "categories")
internal data class DbCategory(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("ordering") val order: Int = 0,
    @ColumnInfo("isVisible") val isVisible: Boolean = true,
    @ColumnInfo("typeSort") val typeSort: String = SortLibraryUtil.ABC,
    @ColumnInfo("isReverseSort") val isReverseSort: Boolean = false,
    @ColumnInfo("spanPortrait") val spanPortrait: Int = 2,
    @ColumnInfo("spanLandscape") val spanLandscape: Int = 3,
)
