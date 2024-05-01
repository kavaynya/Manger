package com.san.kir.data.models.main

import com.san.kir.data.models.base.PlannedTaskBase
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

data class SimplifiedTask(
    override val id: Long = 0L,
    override val manga: String = "",
    override val groupName: String = "",
    override val category: String = "",
    override val catalog: String = "",
    override val type: PlannedType = PlannedType.MANGA,
    val isEnabled: Boolean = false,
    override val period: PlannedPeriod = PlannedPeriod.DAY,
    override val dayOfWeek: PlannedWeek = PlannedWeek.MONDAY,
    override val hour: Int = 0,
    override val minute: Int = 0,
) : PlannedTaskBase
