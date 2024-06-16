package com.san.kir.data.models.main

import android.os.Parcelable
import com.san.kir.data.models.base.PlannedTaskBase
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek
import kotlinx.parcelize.Parcelize

@Parcelize
public data class PlannedTask(
    override val id: Long = 0L,
    val mangaId: Long = -1L,
    override val groupName: String = "",
    val groupContent: List<String> = emptyList(),
    val mangas: List<Long> = emptyList(),
    val categoryId: Long = -1L,
    override val catalog: String = "",
    override val type: PlannedType = PlannedType.MANGA,
    val isEnabled: Boolean = false,
    override val period: PlannedPeriod = PlannedPeriod.DAY,
    override val dayOfWeek: PlannedWeek = PlannedWeek.MONDAY,
    override val hour: Int = 0,
    override val minute: Int = 0,
    val addedTime: Long = 0L,
    val errorMessage: String = "",
    override val manga: String = "",
    override val category: String = "",
) : Parcelable, PlannedTaskBase

public fun PlannedTask.toBase(mangaName: String, categoryName: String): PlannedTaskBase {
    return copy(manga = mangaName, category = categoryName)
}
