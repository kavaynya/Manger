package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.MainMenuType

@Entity(tableName = "mainmenuitems")
internal data class DbMainMenuItem(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("isVisible") val isVisible: Boolean = true,
    @ColumnInfo("order") val order: Int = 0,
    @ColumnInfo("type") val type: MainMenuType = MainMenuType.Default,
)
