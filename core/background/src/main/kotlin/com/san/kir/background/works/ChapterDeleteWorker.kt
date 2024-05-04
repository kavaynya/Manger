package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.models.main.Manga
import kotlin.reflect.KClass

abstract class ChapterDeleteWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "deleteChapter"
        inline fun <reified T : ChapterDeleteWorker> addTask(manga: Manga) =
            addTask(T::class, manga)

        fun addTask(workerClass: KClass<out ListenableWorker>, manga: Manga) {
            val data = workDataOf("id" to manga.id)
            val task = OneTimeWorkRequest.Builder(workerClass.java)
                .addTag(TAG)
                .setInputData(data)
                .build()
            ManualDI.workManager().enqueue(task)
        }
    }
}

