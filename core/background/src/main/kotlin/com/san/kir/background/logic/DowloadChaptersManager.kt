package com.san.kir.background.logic

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.san.kir.background.works.DownloadChaptersWorker
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.repo.ChapterRepository
import com.san.kir.data.db.workers.repo.ChapterWorkerRepository
import com.san.kir.data.models.workers.ChapterTask
import kotlinx.coroutines.flow.first
import java.util.UUID

public class DownloadChaptersManager(
    private val manager: WorkManager,
    private val workerRepository: ChapterWorkerRepository,
    private val chapterRepository: ChapterRepository,
) {
    public suspend fun addTask(chapterId: Long): Operation = withIoContext {
        if (workerRepository.task(chapterId) == null) {
            workerRepository.save(ChapterTask(chapterId = chapterId))
            chapterRepository.addToQueue(chapterId)
        }

        startWorker()
    }

    public suspend fun addTasks(chapterIds: Iterable<Long>): Operation = withIoContext {
        chapterIds.forEach { chapterId ->
            if (workerRepository.task(chapterId) == null) {
                workerRepository.save(ChapterTask(chapterId = chapterId))
                chapterRepository.addToQueue(chapterId)
            }
        }

        startWorker()
    }

    public suspend fun removeTask(chapterId: Long) {
        workerRepository.task(chapterId)?.let {
            workerRepository.remove(it)
        }
        chapterRepository.pauseChapters(listOf(chapterId))
    }

    public suspend fun addPausedTasks() {
        addTasks(chapterRepository.pausedChapters().map { it.id })
    }

    public suspend fun removeAllTasks() {
        val currentTasks = workerRepository.catalog.first()
        workerRepository.remove(currentTasks)
        chapterRepository.pauseChapters(currentTasks.map { it.chapterId })
    }

    private fun startWorker() =
        manager.enqueueUniqueWork(unique, ExistingWorkPolicy.KEEP, task)

    private companion object {
        private val taskId by lazy { UUID.randomUUID() }
        private val unique = "${DownloadChaptersWorker::class.simpleName}UniqueName"
        private val task by lazy {
            OneTimeWorkRequestBuilder<DownloadChaptersWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setId(taskId)
                .build()
        }
    }
}
