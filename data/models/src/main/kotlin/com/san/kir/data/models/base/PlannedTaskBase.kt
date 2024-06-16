package com.san.kir.data.models.base

import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek

public interface PlannedTaskBase {
    public val id: Long
    public val catalog: String
    public val category: String
    public val dayOfWeek: PlannedWeek
    public val groupName: String
    public val hour: Int
    public val minute: Int
    public val manga: String
    public val period: PlannedPeriod
    public val type: PlannedType
}
