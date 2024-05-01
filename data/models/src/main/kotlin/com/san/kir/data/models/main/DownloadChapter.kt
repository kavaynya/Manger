package com.san.kir.data.models.main

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.TimeFormat
import com.san.kir.core.utils.bytesToMb
import com.san.kir.core.utils.format
import com.san.kir.core.utils.fuzzy
import com.san.kir.data.models.utils.DownloadState

data class DownloadItem(
    val id: Long,
    val name: String,
    val manga: String,
    val logo: String,
    val status: DownloadState,
    private val totalTime: Long,
    private val downloadSize: Long,
    val downloadPages: Int,
    val pages: List<String>,
) {
    val progress = if (pages.isNotEmpty()) downloadPages.toFloat() / pages.size else 0F

    val size = bytesToMb(downloadSize).format()

    fun time() = TimeFormat(totalTime / 1000).toString(ManualDI.application)

    val needShowMangaName: Boolean = manga.fuzzy(name).second.not()
}
