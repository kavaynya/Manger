package com.san.kir.data.models.workers

import android.os.Parcelable
import com.san.kir.data.models.base.BaseTask
import com.san.kir.data.models.utils.DownloadState
import kotlinx.parcelize.Parcelize

@Parcelize
public data class ChapterTask(
    override val id: Long = 0L,
    val chapterId: Long = 0L,
    override val state: DownloadState = DownloadState.QUEUED,
    val chapterName: String = "",
    val max: Int = 0,
    val progress: Int = 0,
    val size: Long = 0,
    val time: Long = 0,
) : Parcelable, BaseTask<ChapterTask> {
    override fun setPaused(): ChapterTask = copy(state = DownloadState.PAUSED)
}
