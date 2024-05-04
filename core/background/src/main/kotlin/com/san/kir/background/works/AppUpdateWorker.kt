package com.san.kir.background.works

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.san.kir.background.R
import com.san.kir.background.logic.di.workManager
import com.san.kir.background.util.cancelAction
import com.san.kir.background.util.tryCreateNotificationChannel
import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ID
import com.san.kir.core.utils.ManualDI
import timber.log.Timber
import java.util.regex.Pattern

class AppUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val connectManager: ConnectManager = ManualDI.connectManager()

    private val channelId = "${this::class.java.simpleName}Id"

    private val notificationManager by lazy { NotificationManagerCompat.from(appContext) }

    override suspend fun doWork(): Result {
        applicationContext.tryCreateNotificationChannel(channelId, TAG)

        with(NotificationCompat.Builder(applicationContext, channelId)) {
            setSmallIcon(R.drawable.ic_notification_update)
            setContentTitle(applicationContext.getString(R.string.finding_app_updates))
            setProgress(0, 0, true)
            priority = NotificationCompat.PRIORITY_DEFAULT
            addAction(applicationContext.cancelAction(id))
            kotlin.runCatching { setForeground(ForegroundInfo(notifyId, build())) }
        }

        runCatching {
            val doc = connectManager.getText(URL)
            val texts = doc.split("MANGa readER").last()
            val matcher = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+")
                .matcher(texts)
            if (matcher.find()) {
                val version = matcher.group()
                Timber.v("version = $version")
                val message = if (version != appVersion)
                    applicationContext.getString(
                        R.string.new_version_current_version_format,
                        version,
                        appVersion
                    )
                else
                    applicationContext.getString(R.string.you_have_installed_actual_version)

                with(NotificationCompat.Builder(applicationContext, channelId)) {
                    setSmallIcon(R.drawable.ic_notification_update)
                    setContentTitle(applicationContext.getString(R.string.finding_app_updates))
                    setContentText(message)
                    addAction(openLinkAction())

                    notificationManager.cancel(notifyId)
                    notificationManager.notify(notifyId + 1, build())
                }
            } else throw Throwable("not find in $matcher")
        }.onSuccess {
            return Result.success()
        }.onFailure { ex ->
            Timber.tag("AppUpdateWorker").e(ex)

            with(NotificationCompat.Builder(applicationContext, channelId)) {
                setSmallIcon(R.drawable.ic_notification_update)
                setContentTitle(applicationContext.getString(R.string.finding_app_updates))
                setContentText(applicationContext.getString(R.string.error_during_update_finding))
                notificationManager.cancel(notifyId)
                notificationManager.notify(notifyId + 1, build())
            }
            return Result.failure()
        }

        return Result.retry()
    }

    private val appVersion by lazy {
        kotlin.runCatching {
            applicationContext.packageManager.getPackageInfo(
                applicationContext.packageName, 0
            ).versionName
        }.getOrNull() ?: ""
    }

    private fun openLinkAction(): NotificationCompat.Action {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(URL)
        val cancelAll = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat
            .Action
            .Builder(
                R.drawable.ic_action_search,
                applicationContext.getString(R.string.go_to_4pda),
                cancelAll
            )
            .build()
    }

    companion object {
        private val notifyId = ID.generate()
        private const val URL = "http://4pda.to/forum/index.php?showtopic=772886&st=0#entry53336845"
        private const val TAG = "App Update Finder"
        fun addTask() {
            val deleteManga = OneTimeWorkRequestBuilder<AppUpdateWorker>().build()
            ManualDI.workManager().enqueue(deleteManga)
        }
    }
}
