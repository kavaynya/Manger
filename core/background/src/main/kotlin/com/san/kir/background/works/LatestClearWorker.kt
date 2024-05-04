package com.san.kir.background.works

import androidx.work.OneTimeWorkRequestBuilder
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI


object LatestClearWorkers {
    const val TAG = "cleanLatest"

    fun clearAll() {
        val task = OneTimeWorkRequestBuilder<AllLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }

    fun clearDownloaded() {
        val task = OneTimeWorkRequestBuilder<DownloadedLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }

    fun clearReaded() {
        val task = OneTimeWorkRequestBuilder<ReadLatestClearWorker>()
            .addTag(TAG)
            .build()

        ManualDI.workManager().enqueue(task)
    }
}

