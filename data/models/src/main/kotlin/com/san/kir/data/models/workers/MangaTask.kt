package com.san.kir.data.models.workers

import android.os.Parcelable
import com.san.kir.data.models.base.BaseTask
import com.san.kir.data.models.utils.DownloadState
import kotlinx.parcelize.Parcelize

@Parcelize
public data class MangaTask(
    override val id: Long = 0L,
    val mangaId: Long = 0L,
    val mangaName: String = "",
    val newChapters: Int = 0,
    override val state: DownloadState = DownloadState.QUEUED,
) : Parcelable, BaseTask<MangaTask> {
    override fun setPaused(): MangaTask = copy(state = DownloadState.PAUSED)
}
