package com.san.kir.data.models.base

import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

interface PlannedTaskBase {
    val id: Long
    val catalog: String
    val category: String
    val dayOfWeek: PlannedWeek
    val groupName: String
    val hour: Int
    val minute: Int
    val manga: String
    val period: PlannedPeriod
    val type: PlannedType
}
