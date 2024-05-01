package com.san.kir.data.db.workers.mappers

import com.san.kir.data.db.workers.entities.DbMangaTask
import com.san.kir.data.models.workers.MangaTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbMangaTask.toModel() = MangaTask(id, mangaId, mangaName, newChapters, state)
internal fun MangaTask.toEntity() = DbMangaTask(id, mangaId, mangaName, newChapters, state)
internal fun List<DbMangaTask>.toModels() = map(DbMangaTask::toModel)
internal fun Flow<DbMangaTask?>.toModel() = map { it?.toModel() }
internal fun Flow<List<DbMangaTask>>.toModels() = map { it.toModels() }
