package com.san.kir.background.works

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


object LatestClearWorkers {
    const val TAG = "cleanLatest"

    fun clearAll(ctx: Context) {
        val task = OneTimeWorkRequestBuilder<AllLatestClearWorker>()
            .addTag(TAG)
            .build()

        WorkManager
            .getInstance(ctx)
            .enqueue(task)
    }

    fun clearDownloaded(ctx: Context) {
        val task = OneTimeWorkRequestBuilder<DownloadedLatestClearWorker>()
            .addTag(TAG)
            .build()

        WorkManager
            .getInstance(ctx)
            .enqueue(task)
    }

    fun clearReaded(ctx: Context) {
        val task = OneTimeWorkRequestBuilder<ReadLatestClearWorker>()
            .addTag(TAG)
            .build()

        WorkManager
            .getInstance(ctx)
            .enqueue(task)
    }
}

