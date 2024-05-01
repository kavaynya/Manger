package com.san.kir.data.db.workers.mappers

import com.san.kir.data.db.workers.entities.DbCatalogTask
import com.san.kir.data.models.workers.CatalogTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


internal fun DbCatalogTask.toModel() = CatalogTask(id, name, state, progress)

internal fun CatalogTask.toEntity() = DbCatalogTask(id, name, state, progress)

internal fun List<DbCatalogTask>.toModels() = map(DbCatalogTask::toModel)

internal fun Flow<DbCatalogTask?>.toModel() = map { it?.toModel() }
internal fun Flow<List<DbCatalogTask>>.toModels() = map { it.toModels() }
