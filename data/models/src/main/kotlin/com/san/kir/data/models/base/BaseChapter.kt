package com.san.kir.data.models.base

import com.san.kir.core.utils.TimeFormat
import com.san.kir.core.utils.getCountPagesForChapterInMemory
import com.san.kir.data.models.utils.preparePath
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface BaseChapter {
    val id: Long
    val name: String
    val addedTimestamp: Long
    val downloadPages: Int
    val pages: List<String>
    val path: String
}

val BaseChapter.countPages: Int
    get() = getCountPagesForChapterInMemory(path.preparePath())

val BaseChapter.addedTime: LocalDateTime
    get() = Instant.fromEpochMilliseconds(addedTimestamp).toLocalDateTime(TimeZone.currentSystemDefault())

val BaseChapter.downloadProgress: Int
    get() = if (pages.isEmpty()) 0 else downloadPages * 100 / pages.size
