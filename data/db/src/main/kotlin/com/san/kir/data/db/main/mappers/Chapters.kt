package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.custom.DbDownloadChapter
import com.san.kir.data.db.main.entites.DbChapter
import com.san.kir.data.db.main.views.ViewChapter
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.main.DownloadItem
import com.san.kir.data.models.main.SimplifiedChapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbChapter.toModel() = Chapter(
    id, mangaId, name, date, path, isRead, link, progress, pages, isInUpdate, downloadPages,
    downloadSize, downloadTime, status, order, addedTimestamp
)

@JvmName("toChapterModels")
internal fun List<DbChapter>.toModels() = map(DbChapter::toModel)

@JvmName("toFlowChapterModel")
internal fun Flow<DbChapter?>.toModel() = map { it?.toModel() }

@JvmName("toFlowChapterModels")
internal fun Flow<List<DbChapter>>.toModels() = map { it.toModels() }


internal fun Chapter.toEntity() = DbChapter(
    id, mangaId, name, date, path, isRead, link, progress, pages, isInUpdate, downloadPages,
    downloadSize, downloadTime, status, order, addedTimestamp
)

internal fun List<Chapter>.toEntities() = map(Chapter::toEntity)


internal fun ViewChapter.toModel() = SimplifiedChapter(
    id, status, name, progress, isRead, downloadPages, pages, manga, date, path, addedTimestamp
)

@JvmName("toSimplifiedChapterModels")
internal fun List<ViewChapter>.toModels() = map(ViewChapter::toModel)

@JvmName("toFlowSimplifiedChapterModel")
internal fun Flow<ViewChapter?>.toModel() = map { it?.toModel() }

@JvmName("toFlowSimplifiedChapterModels")
internal fun Flow<List<ViewChapter>>.toModels() = map { it.toModels() }


internal fun DbDownloadChapter.toModel() =
    DownloadItem(id, name, manga, logo, status, totalTime, downloadSize, downloadPages, pages)

@JvmName("toDownloadItemModels")
internal fun List<DbDownloadChapter>.toModels() = map(DbDownloadChapter::toModel)

@JvmName("toFlowDownloadItemModel")
internal fun Flow<DbDownloadChapter?>.toModel() = map { it?.toModel() }

@JvmName("toFlowDownloadItemModels")
internal fun Flow<List<DbDownloadChapter>>.toModels() = map { it.toModels() }
