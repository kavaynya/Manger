package com.san.kir.data.db.workers.mappers

import com.san.kir.data.db.workers.entities.DbChapterTask
import com.san.kir.data.models.workers.ChapterTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbChapterTask.toModel() = ChapterTask(
    id, chapterId, state, chapterName, max, progress, size, time
)

internal fun ChapterTask.toEntity() =
    DbChapterTask(id, chapterId, state, chapterName, max, progress, size, time)

internal fun List<DbChapterTask>.toModels() = map(DbChapterTask::toModel)

internal fun Flow<DbChapterTask?>.toModel() = map { it?.toModel() }
internal fun Flow<List<DbChapterTask>>.toModels() = map { it.toModels() }
