package com.san.kir.data.models.base

import com.san.kir.data.models.utils.DownloadState

public interface BaseTask<T : BaseTask<T>> {
    public val id: Long
    public val state: DownloadState
    public fun setPaused(): T
}
