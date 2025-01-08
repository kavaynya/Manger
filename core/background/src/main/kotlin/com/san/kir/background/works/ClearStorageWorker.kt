package com.san.kir.background.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.san.kir.background.logic.di.workManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.storageRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

public class ClearStorageWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {

    private val storageRepository = ManualDI.storageRepository()

    override suspend fun doWork(): Result = runCatching {
        val storageId = inputData.getLong(ID_KEY, -1)
        val storage = storageRepository.item(storageId) ?: return@runCatching

        getFullPath(storage.path).deleteRecursively()
        storageRepository.delete(storageId)
    }.fold(
        onSuccess = { Result.success() },
        onFailure = {
            Timber.tag(TAG).e(it)
            Result.failure()
        }
    )

    public companion object {
        private const val TAG: String = "clearStorage"
        private const val ID_KEY: String = "id"

        public fun runTask(storageId: Long) {
            val data = workDataOf(ID_KEY to storageId)
            val task = OneTimeWorkRequestBuilder<ClearStorageWorker>()
                .setInputData(data)
                .addTag(TAG)
                .addTag("$TAG|$storageId")
                .build()
            ManualDI.workManager().enqueue(task)
        }

        public fun workInfos(): Flow<List<WorkInfo>> =
            WorkManager.getInstance(ManualDI.application).getWorkInfosByTagFlow(TAG)

        public fun Iterable<String>.findId(): Long? {
            for (tag in this) {
                if (TAG in tag) {
                    tag.split("|").last().toLongOrNull()?.let { return it }
                }
            }
            return null
        }
    }
}
