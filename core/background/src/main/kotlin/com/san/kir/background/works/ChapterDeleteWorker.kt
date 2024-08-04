package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

public abstract class ChapterDeleteWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    public companion object {
        private const val TAG: String = "deleteChapter"
        public inline fun <reified T : ChapterDeleteWorker> addTask(mangaId: Long): Unit =
            addTask(T::class, mangaId)

        public fun addTask(workerClass: KClass<out ListenableWorker>, mangaId: Long) {
            val data = workDataOf("id" to mangaId)
            val task = OneTimeWorkRequest.Builder(workerClass.java)
                .addTag(TAG)
                .setInputData(data)
                .build()
            ManualDI.workManager().enqueue(task)
        }

        public fun workInfos(): Flow<List<WorkInfo>> =
            ManualDI.workManager().getWorkInfosByTagFlow(TAG)
    }
}

