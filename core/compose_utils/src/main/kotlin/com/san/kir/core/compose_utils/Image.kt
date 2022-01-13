package com.san.kir.core.compose_utils

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.san.kir.core.internet.ConnectManager
import java.io.File

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ImageWithStatus(url: String?, context: Context = LocalContext.current) {
    var statusLogo by remember { mutableStateOf(StatusLogo.Init) }
    var logo by remember { mutableStateOf(ImageBitmap(60, 60)) }

    AnimatedVisibility(statusLogo != StatusLogo.Complete) {
        DialogText(
            text = stringResource(
                id = when (statusLogo) {
                    StatusLogo.Init -> R.string.manga_info_dialog_loading
                    StatusLogo.Error -> R.string.manga_info_dialog_loading_failed
                    StatusLogo.None -> R.string.manga_info_dialog_not_image
                    StatusLogo.Complete -> R.string.manga_info_dialog_loading
                }
            )
        )
    }
    AnimatedVisibility(statusLogo == StatusLogo.Complete) {
        Image(logo, null, modifier = Modifier.fillMaxWidth())
    }

    LaunchedEffect(url) {
        if (url != null && url.isNotEmpty()) {

            val manager = ConnectManager(context.applicationContext as Application)

            manager.downloadBitmap(url)?.let { bitmap ->
                logo = bitmap.asImageBitmap()
                statusLogo = StatusLogo.Complete
            } ?: kotlin.run {
                statusLogo = StatusLogo.Error
            }
        } else {
            statusLogo = StatusLogo.None
        }
    }
}

@Composable
inline fun rememberImage(url: String?, context: Context = LocalContext.current): ImageBitmap {
    var logo by remember { mutableStateOf(ImageBitmap(60, 60)) }

    LaunchedEffect(url) {
        if (url != null && url.isNotEmpty()) {
            val manager = ConnectManager(context.applicationContext as Application)
            val name = manager.nameFromUrl(url)

            val imageCacheDirectory = File(context.cacheDir, "image_cache")

            val icon = File(imageCacheDirectory, name)

            kotlin.runCatching {
                logo = BitmapFactory.decodeFile(icon.path).asImageBitmap()
                return@LaunchedEffect
            }

            kotlin.runCatching {
                manager.downloadFile(icon, url, onFinish = { _, _ ->
                    logo = BitmapFactory.decodeFile(icon.path).asImageBitmap()
                })
            }.onFailure {
                ContextCompat.getDrawable(context, R.drawable.unknown)?.let {
                    logo = it.toBitmap().asImageBitmap()
                }
            }
        }
    }
    return logo
}

enum class StatusLogo {
    Init, Complete, Error, None
}
