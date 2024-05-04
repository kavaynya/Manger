package com.san.kir.background.util

import androidx.work.WorkInfo
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import kotlinx.coroutines.flow.Flow

fun workInfoByTag(tag: String): Flow<List<WorkInfo>> {
    return ManualDI.workManager().getWorkInfosByTagFlow(tag)
}
