package com.san.kir.chapters.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.san.kir.chapters.R
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.toast
import com.san.kir.data.models.main.SimplifiedChapter
import com.san.kir.data.models.utils.DownloadState


internal fun onClickItem(
    selectionMode: Boolean,
    chapter: SimplifiedChapter,
    navigateToViewer: (Long) -> Unit,
    sendAction: () -> Unit,
): () -> Unit {
    return {
        if (selectionMode.not()) {
            when (chapter.status) {
                DownloadState.QUEUED,
                DownloadState.LOADING ->
                    ManualDI.application.toast(R.string.open_if_is_downloading)

                else -> navigateToViewer(chapter.id)
            }
        } else {
            sendAction()
        }
    }
}

@Composable
internal fun ChapterName(name: String) {
    Text(
        name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
internal fun WaitingText() {
    Text(
        stringResource(R.string.waiting),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
internal fun LoadingText(progress: Int) {
    Text(
        stringResource(R.string.download_format, progress),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
internal fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(Dimensions.default)
            .padding(end = Dimensions.quarter),
        color = MaterialTheme.colorScheme.secondary,
        strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth - 2.dp,
        strokeCap = StrokeCap.Round
    )
}

@Composable
internal fun DownloadButton(state: DownloadState, sendAction: (Download) -> Unit) {
    FromEndToEndAnimContent(targetState = state) {
        when (it) {
            DownloadState.LOADING,
            DownloadState.QUEUED ->
                // cancel button
                IconButton(
                    onClick = { sendAction(Download.STOP) },
                    modifier = Modifier.padding(end = Dimensions.quarter),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "cancel download button")
                }

            DownloadState.ERROR,
            DownloadState.PAUSED,
            DownloadState.COMPLETED,
            DownloadState.UNKNOWN ->
                Box(modifier = Modifier.padding(end = Dimensions.quarter)) {
                    // download button
                    IconButton(onClick = { sendAction(Download.START) }) {
                        Icon(Icons.Default.Download, contentDescription = "download button")
                    }

                    if (it == DownloadState.ERROR)
                        Icon(
                            painterResource(R.drawable.unknown),
                            contentDescription = "last downloading error",
                            modifier = Modifier
                                .padding(Dimensions.half)
                                .size(Dimensions.default)
                                .align(Alignment.TopEnd),
                            tint = Color.Red
                        )
                }

        }
    }
}

internal enum class Download { START, STOP }
