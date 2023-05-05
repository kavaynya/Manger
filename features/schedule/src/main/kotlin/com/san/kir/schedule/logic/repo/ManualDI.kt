package com.san.kir.schedule.logic.repo

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.categoryDao
import com.san.kir.data.mangaDao
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.plannedDao

internal val ManualDI.tasksRepository: TasksRepository
    get() = TasksRepository(plannedDao, categoryDao, mangaDao, siteCatalogsManager)

internal val ManualDI.updatesRepository: UpdatesRepository
    get() = UpdatesRepository(mangaDao)
