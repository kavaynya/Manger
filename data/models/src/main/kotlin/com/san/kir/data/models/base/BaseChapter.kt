package com.san.kir.data.models.base

import com.san.kir.core.utils.getCountPagesForChapterInMemory
import com.san.kir.data.models.utils.preparePath
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

public interface BaseChapter {
    public val id: Long
    public val name: String
    public val addedTimestamp: Long
    public val downloadPages: Int
    public val pages: List<String>
    public val path: String
}

public val BaseChapter.countPages: Int
    get() = getCountPagesForChapterInMemory(path.preparePath())

public val BaseChapter.addedTime: LocalDateTime
    get() = Instant.fromEpochMilliseconds(addedTimestamp).toLocalDateTime(TimeZone.currentSystemDefault())

public val BaseChapter.downloadProgress: Int
    get() = if (pages.isEmpty()) 0 else downloadPages * 100 / pages.size
