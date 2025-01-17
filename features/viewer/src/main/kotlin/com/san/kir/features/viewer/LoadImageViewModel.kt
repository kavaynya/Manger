package com.san.kir.features.viewer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davemorrissey.labs.subscaleview.ImageSource
import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.utils.convertImagesToPng
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.isOkPng
import com.san.kir.data.models.base.preparedPath
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.features.viewer.logic.SettingsRepository
import com.san.kir.features.viewer.utils.LoadState
import com.san.kir.features.viewer.utils.Page
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class LoadImageViewModel @Inject constructor(
    private val connectManager: ConnectManager,
    private val settingsRepository: SettingsRepository,
    private val siteCatalogsManager: SiteCatalogsManager,
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
        } else {
            // получаем файл страницы
            val name = connectManager.nameFromUrl(page.pagelink)
            val fullPath = getFullPath(page.chapter.preparedPath).absolutePath
            var file = File(fullPath, name)
            file = File(file.parentFile, "${file.nameWithoutExtension}.png")

            if (file.exists() && !force) {

                // Если файл нужного формата в памяти
                if (file.isOkPng()) {
                    _state.update { LoadState.Ready(ImageSource.uri(Uri.fromFile(file))) }
                    return@launch
                }

                // Если файл есть, но формат неверный, то конвертировать
                val png = convertImagesToPng(file)
                if (png.isOkPng()) {
                    _state.update { LoadState.Ready(ImageSource.uri(Uri.fromFile(png))) }
                    return@launch
                }
            }

            val isOnline = settingsRepository.currentViewer().withoutSaveFiles

            // Загрузка файла без сохранения в памяти смартфона
            if (isOnline) {
                if (page.chapter.link.isEmpty()) {
                    _state.update { LoadState.Error(Throwable("No link")) }
                } else {
                    connectManager
                        .downloadBitmap(
                            connectManager.prepareUrl(page.pagelink),
                            runCatching { siteCatalogsManager.getSite(page.pagelink) }
                                .onFailure(Timber.Forest::e)
                                .getOrNull()?.headers,
                            onProgress = { progress ->
                                _state.update { LoadState.Load(progress) }
                            }
                        ).onSuccess { (bm, size, time) ->
                            _state.update {
                                LoadState.Ready(ImageSource.cachedBitmap(bm), size, time)
                            }
                        }.onFailure { ex ->
                            Timber.e(ex)
                            _state.update { LoadState.Error(ex) }
                        }
                }
                return@launch
            }

            // Загрузка файла с сохранением в памяти смартфона
            file = File(fullPath, name)
            connectManager.downloadFile(
                file,
                connectManager.prepareUrl(page.pagelink),
                runCatching { siteCatalogsManager.getSite(page.pagelink) }
                    .onFailure(Timber.Forest::e)
                    .getOrNull()?.headers,
                onProgress = { progress ->
                    _state.update { LoadState.Load(progress) }
                }
            ).onSuccess { (_, length, time) ->
                val imageSource = ImageSource.uri(
                    Uri.fromFile(
                        if (file.extension in arrayOf("gif", "webp", "jpg", "jpeg"))
                            convertImagesToPng(file)
                        else file
                    )
                )
                _state.update { LoadState.Ready(imageSource, length, time) }
            }.onFailure { ex ->
                Timber.e(ex)
                if (ex !is CancellationException)
                    _state.update { LoadState.Error(ex) }
            }
        }
    }
}
