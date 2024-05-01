package com.san.kir.manger.ui.init

import android.content.Context
import android.os.Build
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.manger.logic.di.initRepository
import com.san.kir.manger.logic.repo.InitRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

class InitViewModel(
    private val ctx: Context = ManualDI.context,
    private val repository: InitRepository = ManualDI.initRepository,
) : ViewModel<InitState>(), InitStateHolder {

    override val defaultState = InitState.Memory
    override val tempState = MutableStateFlow<InitState>(defaultState)

    override suspend fun onEvent(event: Action) {
        when (event) {
            is InitEvent.Next -> {
                tempState.update { previous ->
                    when (previous) {
                        InitState.Init -> InitState.Init
                        InitState.Memory -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                InitState.Notification
                            else {
                                startApp()
                                event.onSuccess.invoke()
                                InitState.Init
                            }
                        }
                        InitState.Notification -> {
                            startApp()
                            event.onSuccess.invoke()
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

        if (repository.isFirstLaunch()) repository.restoreSchedule()
    }

    private suspend fun createNeedFolders() = withIoContext {
        DIR.ALL.forEach { dir ->
            Timber.i("dir $dir -> created (${getFullPath(dir).mkdirs()})")
        }
    }
}
