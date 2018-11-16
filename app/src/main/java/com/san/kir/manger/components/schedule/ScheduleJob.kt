package com.san.kir.manger.components.schedule

import com.evernote.android.job.Job
import com.san.kir.manger.components.catalogForOneSite.CatalogForOneSiteUpdaterService
import com.san.kir.manger.components.main.Main
import com.san.kir.manger.extending.ankoExtend.startForegroundService
import com.san.kir.manger.room.dao.getItemsWhere
import com.san.kir.manger.room.models.MangaColumn
import com.san.kir.manger.room.models.PlannedType
import com.san.kir.manger.room.models.mangaList
import com.san.kir.manger.utils.AppUpdateService
import com.san.kir.manger.utils.MangaUpdaterService
import com.san.kir.manger.utils.log

class ScheduleJob(private val tag: String) : Job() {
    override fun onRunJob(params: Params): Result {
        val taskId = tag.toLong()
        val task = Main.db.plannedDao.getItem(taskId)

        try {
            when (task.type) {
                PlannedType.MANGA -> {
                    val manga = Main.db.mangaDao.getItem(task.manga)
                    context.startForegroundService<MangaUpdaterService>(MangaColumn.tableName to manga)
                    ScheduleManager(context).add(task)
                }
                PlannedType.CATEGORY -> {
                    val categories = Main.db.mangaDao.getItemsWhere(task.category)
                    categories.forEach {
                        context.startForegroundService<MangaUpdaterService>(MangaColumn.tableName to it)
                    }
                    ScheduleManager(context).add(task)
                }
                PlannedType.GROUP -> {
                    val group = task.mangaList
                    group.forEach { unic ->
                        val manga = Main.db.mangaDao.getItem(unic)
                        context.startForegroundService<MangaUpdaterService>(MangaColumn.tableName to manga)
                    }
                    ScheduleManager(context).add(task)
                }
                PlannedType.CATALOG -> {
                    val catalog = Main.db.siteDao.getItem(task.catalog)
                    if (catalog != null && !CatalogForOneSiteUpdaterService.isContain(catalog.siteID))
                        context.startForegroundService<CatalogForOneSiteUpdaterService>("id" to catalog.siteID)
                }
                PlannedType.APP -> {
                    context.startForegroundService<AppUpdateService>()
                }
                else -> {
                    log("Тип не соответсвует действительности")
                    return Result.FAILURE
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Result.FAILURE
        }


        return Result.SUCCESS
    }
}

