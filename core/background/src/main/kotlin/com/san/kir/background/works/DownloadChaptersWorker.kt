package com.san.kir.background.works

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.WorkerParameters
import com.san.kir.background.R
import com.san.kir.background.logic.ChapterDownloader
import com.san.kir.background.logic.WorkComplete
import com.san.kir.background.util.cancelAction
import com.san.kir.core.internet.NetworkState
import com.san.kir.core.internet.cellularNetwork
import com.san.kir.core.internet.connectManager
import com.san.kir.core.internet.wifiNetwork
import com.san.kir.core.utils.ID
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.bytesToMb
import com.san.kir.core.utils.format
import com.san.kir.data.chapterRepository
import com.san.kir.data.chapterWorkerRepository
import com.san.kir.data.db.workers.repo.ChapterWorkerRepository
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.data.models.workers.ChapterTask
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.settingsRepository
import com.san.kir.data.statisticsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.takeWhile
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class DownloadChaptersWorker(context: Context, params: WorkerParameters) :
    BaseUpdateWorker<ChapterTask>(context, params) {

    override val TAG: String = "Chapter Downloader"

    override val workerRepository: ChapterWorkerRepository get() = ManualDI.chapterWorkerRepository()
    private val chaptersRepository = ManualDI.chapterRepository()
    private val settingsRepository = ManualDI.settingsRepository()
    private val statisticsRepository = ManualDI.statisticsRepository()
    private val connectManager = ManualDI.connectManager()
    private val siteCatalogsManager = ManualDI.siteCatalogsManager()
    private val cellularNetwork = ManualDI.cellularNetwork()
    private val wifiNetwork = ManualDI.wifiNetwork()

    private var successfuled = listOf<ChapterTask>()
    private var networkState = NetworkState.OK

    override suspend fun work(task: ChapterTask) {
        val loader = ChapterDownloader(
            chapter = chaptersRepository.item(task.chapterId),
            chapterRepository = chaptersRepository,
            statisticsRepository = statisticsRepository,
            connectManager = connectManager,
            siteCatalogsManager = siteCatalogsManager,
            concurrent = if (settingsRepository.concurrent()) 4 else 1,
            checkNetwork = ::awaitNetwork,
        ) { chapter ->
            updateCurrentTask {
                copy(
                    chapterName = chapter.name,
                    max = chapter.pages.size,
                    progress = chapter.downloadPages,
                    size = chapter.downloadSize,
                    time = chapter.downloadTime,
                    state = chapter.status,
                )
            }
            notify()
        }

        awaitNetwork()
        loader.run()
            .onSuccess {
                withCurrentTask { task ->
                    successfuled = successfuled + task
                }
            }
            .onFailure {
                if (settingsRepository.retry()) {
                    chaptersRepository.addToQueue(task.chapterId)
                } else withCurrentTask { task ->
                    errored = errored + task
                }
            }
    }

    override suspend fun onNotify(task: ChapterTask?) {
        with(NotificationCompat.Builder(applicationContext, channelId)) {
            setSmallIcon(R.drawable.ic_notification_download)

            setContentTitle(applicationContext.getString(R.string.chapters_downloading))

            when (networkState) {
                NetworkState.NOT_WIFI -> setContentText(applicationContext.getString(R.string.wifi_off))
                NetworkState.NOT_CELLULAR -> setContentText(applicationContext.getString(R.string.internet_off))
                NetworkState.OK -> task?.let { task ->
                    setContentTitle(
                        applicationContext.getString(R.string.queue_downloading, queue.size)
                    )
                    setContentText(task.chapterName)

                    when (task.state) {
                        DownloadState.LOADING -> setProgress(task.max, task.progress, false)
                        else -> setProgress(0, 0, true)
                    }

                    setSubText(messageToGo)
                } ?: kotlin.run {
                    setContentText(messageToGo)
                }
            }

            actionToDownloads?.let(::setContentIntent)

            priority = NotificationCompat.PRIORITY_DEFAULT

            addAction(applicationContext.cancelAction(id))

            kotlin.runCatching { setForeground(notifyId, build()) }
        }
    }

    override fun finishedNotify(ex: Throwable?) {
        val builder = NotificationCompat.Builder(applicationContext, channelId)
        builder.setSmallIcon(R.drawable.ic_notification_download)

        if (ex is WorkComplete) {
            when {
                successfuled.isEmpty() && errored.isNotEmpty() -> {
                    builder.setContentTitle(applicationContext.getString(R.string.download_failed))
                    builder.setContentText(applicationContext.getString(R.string.all_chapters_downloaded_with_an_error))
                }

                successfuled.isNotEmpty() && errored.isNotEmpty() -> {
                    builder.setContentTitle(applicationContext.getString(R.string.download_complete_with_error))
                    builder.setContentText(applicationContext.getString(R.string.all_chapters_downloaded_with_an_error))

                    builder.setStyle(
                        NotificationCompat.InboxStyle()
                            .addLine(
                                applicationContext.resources.getQuantityString(
                                    R.plurals.chapters_download_complete_without_errors,
                                    successfuled.size,
                                    successfuled.size
                                )
                            )
                            .addLine(
                                applicationContext.resources.getQuantityString(
                                    R.plurals.chapters_with_errors,
                                    errored.size,
                                    errored.size
                                )
                            )
                            .addLine(sizeAndTime())
                    )
                }

                successfuled.isNotEmpty() -> {
                    builder.setContentTitle(applicationContext.getString(R.string.download_complete))
                    builder.setContentText(applicationContext.getString(R.string.enjoy_reading))

                    builder.setStyle(
                        NotificationCompat.InboxStyle()
                            .addLine(
                                applicationContext.resources.getQuantityString(
                                    R.plurals.chapters_download_complete_without_errors,
                                    successfuled.size,
                                    successfuled.size
                                )
                            )
                            .addLine(sizeAndTime())
                    )
                }
            }
        } else {
            builder.setContentTitle(applicationContext.getString(R.string.chapters_downloading_canceled))
            builder.setStyle(
                NotificationCompat.InboxStyle()
                    .addLine(
                        applicationContext.resources.getQuantityString(
                            R.plurals.chapters_download_complete_without_errors,
                            successfuled.size,
                            successfuled.size
                        )
                    )
                    .addLine(sizeAndTime())
            )
        }

        actionToDownloads?.let(builder::setContentIntent)

        notificationManager.cancel(notifyId)
        notificationManager.notify(notifyId + 1, builder.build())
    }

    override suspend fun onRemoveAllTasks(tasks: List<ChapterTask>) {
        chaptersRepository.pauseChapters(tasks.map { it.chapterId })
    }

    private fun sizeAndTime(): String {
        val minutes = successfuled.sumOf { it.time } / 1.minutes.inWholeMilliseconds
        return if (minutes < 1)
            applicationContext.getString(
                R.string.download_mb, bytesToMb(successfuled.sumOf { it.size }).format(),
            )
        else
            applicationContext.getString(
                R.string.download_mb_by_min,
                bytesToMb(successfuled.sumOf { it.size }).format(),
                minutes
            )
    }


    private val messageToGo by lazy {
        applicationContext.getString(R.string.press_notify_for_go_to_downloads)
    }

    // Если нет был в наличии, то вернется true
    // Если его включения было необходимо ожидать, то false
    private suspend fun awaitNetwork(): Boolean {
        delay(1.seconds) // Задержка, чтобы успело отработать оповещение от системы, если изменился статус сети
        if (settingsRepository.wifi()) {
            if (wifiNetwork.state.value.not()) {
                networkState = NetworkState.NOT_WIFI
                notify()

                wifiNetwork.state.takeWhile { it.not() }.collect()

                networkState = NetworkState.OK
                return false
            }
        } else {
            if (cellularNetwork.state.value.not() || wifiNetwork.state.value.not()) {
                networkState = NetworkState.NOT_CELLULAR
                notify()

                combine(cellularNetwork.state, wifiNetwork.state) { cell, wifi -> cell || wifi }
                    .takeWhile { it.not() }.collect()

                networkState = NetworkState.OK
                return false
            }
        }
        networkState = NetworkState.OK
        return true
    }

    public companion object {
        private val notifyId = ID.generate()

        private var actionToDownloads: PendingIntent? = null

        public fun setDownloadDeepLink(ctx: Context, deepLinkIntent: Intent) {
            actionToDownloads = TaskStackBuilder.create(ctx).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }
    }
}
