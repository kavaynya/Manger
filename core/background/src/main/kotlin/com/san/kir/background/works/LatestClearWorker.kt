package com.san.kir.background.works

import androidx.work.OneTimeWorkRequestBuilder
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI


public object LatestClearWorkers {
    public const val TAG: String = "cleanLatest"

    public fun clearAll() {
        val task = OneTimeWorkRequestBuilder<AllLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }

    public fun clearDownloaded() {
        val task = OneTimeWorkRequestBuilder<DownloadedLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }

    public fun clearRead() {
        val task = OneTimeWorkRequestBuilder<ReadLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }
}

