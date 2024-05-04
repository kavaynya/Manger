package com.san.kir.background.logic

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.san.kir.background.works.UpdateCatalogWorker
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.workers.repo.CatalogWorkerRepository
import com.san.kir.data.models.workers.CatalogTask
import java.util.UUID

class UpdateCatalogManager(
    private val manager: WorkManager,
    private val workerRepository: CatalogWorkerRepository,
) {

    suspend fun addTask(name: String) = withIoContext {
        if (workerRepository.task(name) == null)
            workerRepository.save(CatalogTask(name = name))

        startWorker()
    }

    private fun startWorker() =
        manager.enqueueUniqueWork(unique, ExistingWorkPolicy.KEEP, task)

    suspend fun removeTask(name: String) = withIoContext {
        workerRepository.task(name)?.let {
            workerRepository.remove(it)
        }
    }

    fun loadTasks() = workerRepository.catalog
    fun loadTask(name: String) = workerRepository.loadTask(name)

    companion object {
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
