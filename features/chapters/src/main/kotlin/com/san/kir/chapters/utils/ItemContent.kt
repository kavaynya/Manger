package com.san.kir.chapters.utils

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.san.kir.chapters.R
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.data.models.utils.DownloadState
import com.san.kir.core.utils.toast
import com.san.kir.data.models.extend.SimplifiedChapter

internal fun onClickItem(
    context: Context,
    selectionMode: Boolean,
    chapter: SimplifiedChapter,
    navigateToViewer: (Long) -> Unit,
    sendEvent: () -> Unit,
): () -> Unit {
    return {
        if (selectionMode.not()) {
            when (chapter.status) {
                com.san.kir.data.models.utils.DownloadState.QUEUED,
                com.san.kir.data.models.utils.DownloadState.LOADING,
                     ->
                    context.toast(R.string.list_chapters_open_is_download)

                else -> navigateToViewer(chapter.id)
            }
        } else {
            sendEvent()
        }
    }
}

@Composable
internal fun ChapterName(name: String) {
    Text(
        name,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun RowScope.ChapterDate(date: String) {
    Text(
        date,
        style = MaterialTheme.typography.body2,
        modifier = Modifier.alignByBaseline(),
    )
}

@Composable
internal fun WaitingText() {
    Text(
        stringResource(R.string.list_chapters_queue),
        style = MaterialTheme.typography.body2,
    )
}

@Composable
internal fun LoadingText(progress: Int) {
    Text(
        stringResource(R.string.list_chapters_download_progress, progress),
        style = MaterialTheme.typography.body2,
    )
}

@Composable
internal fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(Dimensions.bigger)
            .padding(end = Dimensions.quarter),
        strokeWidth = ProgressIndicatorDefaults.StrokeWidth - 1.dp
    )
}

@Composable
internal fun DownloadButton(state: com.san.kir.data.models.utils.DownloadState, sendEvent: (Download) -> Unit) {
    FromEndToEndAnimContent(targetState = state) {
        when (it) {
            com.san.kir.data.models.utils.DownloadState.LOADING,
            com.san.kir.data.models.utils.DownloadState.QUEUED,
            ->
                // cancel button
                IconButton(onClick = { sendEvent(Download.STOP) }) {
                    Icon(Icons.Default.Close, contentDescription = "cancel download button")
                }

            com.san.kir.data.models.utils.DownloadState.ERROR,
            com.san.kir.data.models.utils.DownloadState.PAUSED,
            com.san.kir.data.models.utils.DownloadState.COMPLETED,
            com.san.kir.data.models.utils.DownloadState.UNKNOWN,
            -> Box {
                // download button
                IconButton(onClick = { sendEvent(Download.START) }) {
                    Icon(Icons.Default.Download, contentDescription = "download button")
                }

                if (it == com.san.kir.data.models.utils.DownloadState.ERROR)
                    Icon(
                        painterResource(com.san.kir.core.compose.R.drawable.unknown),
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
