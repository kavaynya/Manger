package com.san.kir.data.models.workers

import android.os.Parcelable
import com.san.kir.data.models.base.BaseTask
import com.san.kir.data.models.utils.DownloadState
import kotlinx.parcelize.Parcelize

@Parcelize
data class CatalogTask(
    override val id: Long = 0L,
    val name: String = "",
    override val state: DownloadState = DownloadState.QUEUED,
    val progress: Float = 0f,
) : Parcelable, BaseTask<CatalogTask> {
    override fun setPaused() = copy(state = DownloadState.PAUSED)
}
