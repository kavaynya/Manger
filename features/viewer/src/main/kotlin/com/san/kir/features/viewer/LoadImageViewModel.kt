package com.san.kir.features.viewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davemorrissey.labs.subscaleview.ImageSource
import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.convertImagesToPng
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.isOkPng
import com.san.kir.data.db.main.repo.SettingsRepository
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.settingsRepository
import com.san.kir.features.viewer.utils.LoadState
import com.san.kir.features.viewer.utils.Page
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

internal class LoadImageViewModel(
    private val connectManager: ConnectManager = ManualDI.connectManager(),
    private val settingsRepository: SettingsRepository = ManualDI.settingsRepository(),
    private val siteCatalogsManager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
) : ViewModel() {
    private var imageLoadingJob: Job? = null

    private val _state = MutableStateFlow<LoadState>(LoadState.Init)
    val state = _state.asStateFlow()

    fun setInitState() {
        _state.update { LoadState.Init }
    }

    fun load(page: Page.Current?, force: Boolean = false) {
        if (force) {
            Timber.v("cancel job")
            imageLoadingJob?.cancel()
        }

        if (imageLoadingJob?.isActive == true) {
            Timber.v("job is active")
            return
        }

        imageLoadingJob = startLoadImage(page, force)
    }

    private fun startLoadImage(
        page: Page.Current?,
        force: Boolean = false,
    ) = viewModelScope.launch {
        _state.update { LoadState.Init }

        if (page == null) {
            _state.update { LoadState.Error(Throwable("None page")) }
            return@launch
        }

        // получаем файл страницы
        val name = connectManager.nameFromUrl(page.pagelink)
        val fullPath = getFullPath(page.chapter.path).absolutePath
        var file = File(fullPath, name)
        file = File(file.parentFile, "${file.nameWithoutExtension}.png")

        if (file.exists() && !force) {

            if (!file.isOkPng()) {
                file = convertImagesToPng(file)
            }

            if (file.isOkPng()) {
                _state.update { LoadState.Ready(ImageSource.uri(Uri.fromFile(file))) }
                return@launch
            }
        }

        // Загрузка файла без сохранения в памяти смартфона
        if (settingsRepository.withoutSaveFiles()) {
            if (page.chapter.link.isEmpty()) {
                _state.update { LoadState.Error(Throwable("No link")) }
                return@launch
            }

            connectManager.downloadBitmap(
                url = connectManager.prepareUrl(page.pagelink),
                headers = siteCatalogsManager.headersByLink(page.pagelink),
                onProgress = { progress -> _state.update { LoadState.Load(progress) } }
            ).onSuccess { (bm, size, time) ->
                _state.update {
                    LoadState.Ready(ImageSource.cachedBitmap(bm), size, time)
                }
            }.onFailure { ex ->
                Timber.e(ex)
                _state.update { LoadState.Error(ex) }
            }
            return@launch
        }

        // Загрузка файла с сохранением в памяти смартфона
        file = File(fullPath, name)
        connectManager.downloadFile(
            file = file,
            url = connectManager.prepareUrl(page.pagelink),
            headers = siteCatalogsManager.headersByLink(page.pagelink),
            onProgress = { progress -> _state.update { LoadState.Load(progress) } }
        ).onSuccess { (_, length, time) ->
            val uri = Uri.fromFile(
                if (file.extension in arrayOf("gif", "webp", "jpg", "jpeg"))
                    convertImagesToPng(file)
                else file
            )
            val imageSource = ImageSource.uri(uri)
            _state.update { LoadState.Ready(imageSource, length, time) }
        }.onFailure { ex ->
            Timber.e(ex)
            if (ex !is CancellationException)
                _state.update { LoadState.Error(ex) }
        }
    }
}
