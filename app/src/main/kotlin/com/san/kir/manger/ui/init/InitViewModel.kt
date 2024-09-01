package com.san.kir.manger.ui.init

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import com.san.kir.background.works.ScheduleWorker
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.db.main.repo.PlannedRepository
import com.san.kir.data.lazyPlannedRepository
import com.san.kir.data.models.main.SimplifiedTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

internal class InitViewModel(
    private val ctx: Context = ManualDI.application,
    private val plannedRepository: Lazy<PlannedRepository> = ManualDI.lazyPlannedRepository(),
) : ViewModel<InitState>(), InitStateHolder {

    private val sharedPreferences by lazy {
        ctx.getSharedPreferences("startup", Context.MODE_PRIVATE)
    }

    private val isFirstLaunch: Boolean
        get() {
            if (sharedPreferences.contains("firstLaunch")) return false
            sharedPreferences.edit { putBoolean("firstLaunch", false) }
            return true
        }

    override val defaultState = InitState.Memory
    override val tempState = MutableStateFlow(defaultState)

    override suspend fun onAction(action: Action) {
        when (action) {
            is InitEvent.Next -> {
                tempState.update { previous ->
                    when (previous) {
                        InitState.Init -> InitState.Init
                        InitState.Memory -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                InitState.Notification
                            else {
                                startApp()
                                action.onSuccess.invoke()
                                InitState.Init
                            }
                        }

                        InitState.Notification -> {
                            startApp()
                            action.onSuccess.invoke()
                            InitState.Init
                        }
                    }
                }
            }
        }
    }

    private suspend fun startApp() = withDefaultContext {
        createNeedFolders()

//        UpdateMangaWorker.setLatestDeepLink(
//            ctx, ctx.deepLinkIntent<MainActivity>(MainNavTarget.Latest),
//        )
//
//        UpdateCatalogWorker.setLatestDeepLink(
//            ctx, ctx.deepLinkIntent<MainActivity>(CatalogsNavTarget.Main)
//        )
//

//        DownloadChaptersWorker.setDownloadDeepLink(
//            ctx, ctx.deepLinkIntent<MainActivity>(MainNavTarget.Downloader)
//        )

        delay(0.5.seconds)

        if (isFirstLaunch) {
            restoreSchedule()
        }
    }

    private suspend fun createNeedFolders() = withIoContext {
        DIR.ALL.forEach { dir ->
            Timber.i("dir $dir -> created (${getFullPath(dir).mkdirs()})")
        }
    }

    private suspend fun restoreSchedule() = withIoContext {
        plannedRepository.value.simplifiedItems.first()
            .filter(SimplifiedTask::isEnabled)
            .forEach(ScheduleWorker.Companion::addTask)
    }
}
