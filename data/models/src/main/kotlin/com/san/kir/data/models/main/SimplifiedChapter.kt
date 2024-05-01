package com.san.kir.data.models.main

import com.san.kir.data.models.base.BaseChapter
import com.san.kir.data.models.utils.DownloadState

data class SimplifiedChapter(
    override val id: Long = 0,
    val status: DownloadState,
    override val name: String,
    val progress: Int,
    val isRead: Boolean,
    override val downloadPages: Int,
    override val pages: List<String>,
    val manga: String,
    val date: String,
    override val path: String,
    override val addedTimestamp: Long
): BaseChapter
