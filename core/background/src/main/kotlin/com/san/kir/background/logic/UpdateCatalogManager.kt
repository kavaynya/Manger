package com.san.kir.background.logic

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.san.kir.background.works.UpdateCatalogWorker
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.repo.CatalogWorkerRepository
import com.san.kir.data.models.workers.CatalogTask
import kotlinx.coroutines.flow.Flow
import java.util.UUID

public class UpdateCatalogManager(
    private val manager: WorkManager,
    private val workerRepository: CatalogWorkerRepository,
) {

    public suspend fun addTask(name: String): Operation = withIoContext {
        if (workerRepository.task(name) == null)
            workerRepository.save(CatalogTask(name = name))

        startWorker()
    }

    private fun startWorker() =
        manager.enqueueUniqueWork(unique, ExistingWorkPolicy.KEEP, task)

    public suspend fun removeTask(name: String): Unit? = withIoContext {
        workerRepository.task(name)?.let {
            workerRepository.remove(it)
        }
    }

    public fun loadTasks(): Flow<List<CatalogTask>> = workerRepository.catalog
    public fun loadTask(name: String): Flow<CatalogTask?> = workerRepository.loadTask(name)

    private companion object {
        private val taskId by lazy { UUID.randomUUID() }
        private val unique = "${UpdateCatalogWorker::class.simpleName}UniqueName"
        private val task by lazy {
            OneTimeWorkRequestBuilder<UpdateCatalogWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setId(taskId)
                .build()
        }
    }
}
