package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

@Entity(tableName = "planned_task")
internal data class DbPlannedTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Long = 0L,
    @ColumnInfo("manga_id") val mangaId: Long = -1L,
    @ColumnInfo("group_name") val groupName: String = "",
    @ColumnInfo("group_content") val groupContent: List<String> = emptyList(),
    @ColumnInfo("mangas", defaultValue = "") val mangas: List<Long> = emptyList(),
    @ColumnInfo("category_id") val categoryId: Long = -1L,
    @ColumnInfo("catalog") val catalog: String = "",
    @ColumnInfo("type") val type: PlannedType = PlannedType.MANGA,
    @ColumnInfo("is_enabled") val isEnabled: Boolean = false,
    @ColumnInfo("period") val period: PlannedPeriod = PlannedPeriod.DAY,
    @ColumnInfo("day_of_week") val dayOfWeek: PlannedWeek = PlannedWeek.MONDAY,
    @ColumnInfo("hour") val hour: Int = 0,
    @ColumnInfo("minute") val minute: Int = 0,
    @ColumnInfo("added_time") val addedTime: Long = 0L,
    @ColumnInfo("error_message") val errorMessage: String = "",
)
