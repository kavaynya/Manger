package com.san.kir.background.works

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.san.kir.background.R
import com.san.kir.background.logic.WorkComplete
import com.san.kir.background.logic.di.catalogRepository
import com.san.kir.background.logic.di.catalogWorkerRepository
import com.san.kir.background.logic.repo.CatalogRepository
import com.san.kir.background.logic.repo.CatalogWorkerRepository
import com.san.kir.background.util.cancelAction
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.core.utils.ID
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.models.base.CatalogTask
import com.san.kir.data.models.base.SiteCatalogElement
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import kotlinx.coroutines.flow.collectIndexed
import timber.log.Timber

class UpdateCatalogWorker(
    context: Context,
    params: WorkerParameters,
) : BaseUpdateWorker<CatalogTask>(context, params) {

    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager
    private val catalogRepository: CatalogRepository = ManualDI.catalogRepository

    override val workerRepository: CatalogWorkerRepository = ManualDI.catalogWorkerRepository
    override val TAG = "Catalogs Updater"

    override suspend fun work(task: CatalogTask) {
        updateCurrentTask { copy(progress = 0f, state = DownloadState.QUEUED) }
        notify()

        val tempList = mutableListOf<SiteCatalogElement>()
        kotlin.runCatching {
            val site = manager.catalog.first { it.name == task.name }
            site.init()

            var retry = 3
            while (retry != 0) {
                retry--

                tempList.clear()

                updateCurrentTask { copy(progress = 0f, state = DownloadState.LOADING) }
                notify()

                site.catalog()
                    .collectIndexed { index, value ->
                        val new = index / site.volume.toFloat()

                        withCurrentTask { t ->
                            if ((new * 100).toInt() > (t.progress * 100).toInt()) {
                                updateCurrentTask { copy(progress = new) }
                                notify()
                                Timber.v("task -> ${task.name} / $index / ${site.volume}")
                            }
                        }

                        tempList.add(value)
                    }
                if (tempList.size >= site.volume - 10) break
            }

            Timber.v("update finish. elements getting ${tempList.size}")

            withCurrentTask { t ->
                updateCurrentTask { copy(state = DownloadState.COMPLETED) }
                notify()
            }

            catalogRepository.save(task.name, tempList)

            Timber.v("save items in db")
        }.onFailure {
            errored = errored + task
            Timber.e(it)
        }
    }

    override suspend fun onNotify(task: CatalogTask?) {
        with(NotificationCompat.Builder(applicationContext, channelId)) {
            setSmallIcon(R.drawable.ic_notification_update)

            setContentTitle(
                applicationContext.getString(R.string.catalogs_updating_format, queue.size)
            )

            task?.let { task ->
                when (task.state) {
                    DownloadState.LOADING -> {
                        val percent = (task.progress * 100).toInt()
                        setContentText("${task.name}  ${percent}%")
                        setProgress(100, percent, false)
                    }

                    DownloadState.QUEUED -> {
                        setContentText(
                            applicationContext.getString(
                                R.string.prepare_catalog_for_loading_format, task.name
                            )
                        )
                        setProgress(0, 0, true)
                    }

                    DownloadState.PAUSED -> {
                        setContentText(
                            applicationContext.getString(
                                R.string.cancel_loading_catalog_format, task.name
                            )
                        )
                        setProgress(0, 0, true)
                    }

                    DownloadState.COMPLETED -> {
                        setContentText(
                            applicationContext.getString(
                                R.string.saving_catalog_format, task.name
                            )
                        )
                        setProgress(0, 0, true)
                    }

                    else -> {}
                }

                workerRepository.update(task)

                setSubText(messageToGo)
            } ?: kotlin.run {
                setContentText(messageToGo)
            }

            actionGoToCatalogs?.let {
                setContentIntent(it)
            }

            priority = NotificationCompat.PRIORITY_DEFAULT

            addAction(applicationContext.cancelAction(id))

            kotlin.runCatching {
                setForeground(ForegroundInfo(notifyId, build()))
            }
        }
    }

    override fun finishedNotify(ex: Throwable?) {
        with(NotificationCompat.Builder(applicationContext, channelId)) {
            setSmallIcon(R.drawable.ic_notification_update)

            if (ex is WorkComplete) {
                if (errored.isEmpty()) {
                    setContentTitle(applicationContext.getString(R.string.complete_catalog_update))
                } else {
                    setContentTitle(applicationContext.getString(R.string.updating_catalogs_completed_with_error))
                    setStyle(
                        NotificationCompat.BigTextStyle().bigText(errored.joinToString { it.name })
                    )
                }
            } else {
                setContentTitle(applicationContext.getString(R.string.updating_catalogs_canceled))
            }

            actionGoToCatalogs?.let {
                setContentIntent(it)
            }

            notificationManager.cancel(notifyId)
            notificationManager.notify(notifyId + 1, build())
        }
    }

    private val messageToGo by lazy {
        applicationContext.getString(R.string.press_notify_for_go_to_catalogs)
    }

    companion object {
        private val notifyId = ID.generate()

        private var actionGoToCatalogs: PendingIntent? = null

        fun setLatestDeepLink(ctx: Context, deepLinkIntent: Intent) {
            actionGoToCatalogs = TaskStackBuilder.create(ctx).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }
    }
}
