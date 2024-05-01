package com.san.kir.manger.logic.repo

import android.content.Context
import androidx.core.content.edit
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.PlannedDao
import kotlinx.coroutines.flow.first

class InitRepository(
    private val ctx: Context,
    private val plannedDao: Lazy<PlannedDao>,
) {

    private val sharedPreferences by lazy {
        ctx.getSharedPreferences("startup", Context.MODE_PRIVATE)
    }

    fun isFirstLaunch(): Boolean {
        if (sharedPreferences.contains("firstLaunch"))
            return false

        sharedPreferences.edit {
            putBoolean("startup", false)
        }
        return true
    }

    suspend fun restoreSchedule() = withIoContext {
        plannedDao.value
            .loadSimpleItems()
            .first()
            .filter { it.isEnabled }
            .forEach { ScheduleWorker.addTask(ctx, it) }
    }
}
