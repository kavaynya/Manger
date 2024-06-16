package com.san.kir.core.compose

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.internet.connectManager
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.withDefaultContext
import timber.log.Timber
import java.io.File

@Composable
public fun ImageWithStatus(url: String?, modifier: Modifier = Modifier) {
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
    BottomAnimatedVisibility(statusLogo == StatusLogo.Complete, modifier = modifier) {
        Image(logo, null, modifier = Modifier.fillMaxWidth())
    }

    LaunchedEffect(url) {
        Timber.w("LaunchedEffect -> ImageWithStatus($url)")
        if (!url.isNullOrEmpty()) {
            statusLogo = StatusLogo.Init
            ManualDI.connectManager()
                .downloadBitmap(url)
                .onSuccess { (bitmap, _, _) ->
                    logo = bitmap.asImageBitmap()
                    statusLogo = StatusLogo.Complete
                }
                .onFailure { statusLogo = StatusLogo.Error }
        } else {
            statusLogo = StatusLogo.None
        }
    }
}

@Composable
public fun rememberImage(url: String?): BitmapPainter {
    val context = LocalContext.current
    var logo by remember { mutableStateOf(BitmapPainter(ImageBitmap(2, 2))) }

    LaunchedEffect(url) {
        Timber.w("LaunchedEffect -> rememberImage($url) logo($logo)")
        withDefaultContext {
            if (url != null && url.isNotEmpty()) {
                val name = ManualDI.connectManager().nameFromUrl2(url)
                val imageCacheDirectory = File(context.cacheDir, "image_cache")
                val icon = File(imageCacheDirectory, name)

                Timber.v("remember image with path ${icon.path}")

                kotlin.runCatching {
                    logo = BitmapPainter(BitmapFactory.decodeFile(icon.path).asImageBitmap())
                    return@withDefaultContext
                }

                ManualDI.connectManager().downloadFile(icon, url)
                    .mapCatching {
                        logo = BitmapPainter(BitmapFactory.decodeFile(icon.path).asImageBitmap())
                    }
                    .onFailure {
                        ContextCompat.getDrawable(context, R.drawable.unknown)?.let { draw ->
                            logo = BitmapPainter(draw.toBitmap().asImageBitmap())
                        }
                    }
            }
        }
    }
    return logo
}

private enum class StatusLogo {
    Init, Complete, Error, None
}

@Composable
public fun CircleLogo(
    logoUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = Dimensions.Image.bigger
) {
    Image(
        rememberImage(logoUrl),
        contentDescription = "",
        modifier = modifier
            .padding(Dimensions.smallest)
            .clip(CircleShape)
            .size(size),
        contentScale = ContentScale.Crop
    )
}
