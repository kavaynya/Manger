package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.custom.DbSimplifiedPlannedTask
import com.san.kir.data.db.main.entites.DbPlannedTask
import com.san.kir.data.models.main.PlannedTask
import com.san.kir.data.models.main.SimplifiedTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbPlannedTask.toModel() = PlannedTask(
    id, mangaId, groupName, groupContent, mangas, categoryId, catalog, type, isEnabled, period,
    dayOfWeek, hour, minute, addedTime, errorMessage
)

@JvmName("toPlannedTaskModels")
internal fun List<DbPlannedTask>.toModels() = map(DbPlannedTask::toModel)

@JvmName("toFlowPlannedTaskModel")
internal fun Flow<DbPlannedTask?>.toModel() = map { it?.toModel() }

@JvmName("toFlowPlannedTaskModels")
internal fun Flow<List<DbPlannedTask>>.toModels() = map { it.toModels() }


internal fun PlannedTask.toEntity() = DbPlannedTask(
    id, mangaId, groupName, groupContent, mangas, categoryId, catalog, type, isEnabled, period,
    dayOfWeek, hour, minute, addedTime, errorMessage
)


internal fun DbSimplifiedPlannedTask.toModel() = SimplifiedTask(
    id, manga, groupName, category, catalog, type, isEnabled, period, dayOfWeek, hour, minute
)

@JvmName("toSimplifiedTaskModels")
internal fun List<DbSimplifiedPlannedTask>.toModels() = map(DbSimplifiedPlannedTask::toModel)

@JvmName("toFlowSimplifiedTaskModel")
internal fun Flow<DbSimplifiedPlannedTask?>.toModel() = map { it?.toModel() }

@JvmName("toFlowSimplifiedTaskModels")
internal fun Flow<List<DbSimplifiedPlannedTask>>.toModels() = map { it.toModels() }
