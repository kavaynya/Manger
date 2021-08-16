package com.san.kir.manger.ui.application_navigation.chapters

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.san.kir.ankofork.dialogs.longToast
import com.san.kir.ankofork.dialogs.toast
import com.san.kir.manger.R
import com.san.kir.manger.room.entities.Chapter
import com.san.kir.manger.room.entities.DownloadItem
import com.san.kir.manger.room.entities.countPages
import com.san.kir.manger.room.entities.toDownloadItem
import com.san.kir.manger.services.DownloadService
import com.san.kir.manger.utils.enums.DownloadStatus
import com.san.kir.manger.utils.extensions.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChaptersItemContent(
    chapter: Chapter,
    isSelected: Boolean,
    index: Int,
    nav: NavHostController,
    viewModel: ChaptersViewModel,
    context: Context = LocalContext.current,
) {
    val selectionMode by viewModel.selectionMode.collectAsState()
    val downloadItem by viewModel.getDownloadItem(chapter).collectAsState(DownloadItem())

    var countPagesInMemory by remember { mutableStateOf(0) }
    val deleteIndicator by remember(countPagesInMemory) { mutableStateOf(countPagesInMemory > 0) }

    val downloadIndicator by remember(downloadItem) {
        mutableStateOf(
            downloadItem.status == DownloadStatus.queued
                    || downloadItem.status == DownloadStatus.loading
        )
    }
    val queueIndicator by remember(downloadItem) {
        mutableStateOf(downloadItem.status == DownloadStatus.queued)
    }
    val loadingIndicator by remember(downloadItem) {
        mutableStateOf(downloadItem.status == DownloadStatus.loading)
    }
    val downloadPercent by remember(downloadItem) {
        mutableStateOf(
            if (downloadItem.totalPages == 0) 0
            else downloadItem.downloadPages * 100 / downloadItem.totalPages
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                when {
                    isSelected -> Color(0x9934b5e4)
                    chapter.isRead -> Color(0xffa5a2a2)
                    else -> Color.Transparent
                }
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (selectionMode.not()) {
                        if (downloadIndicator) {
                            context.toast(R.string.list_chapters_open_is_download)
                        } else {
                            if (chapter.pages.isNullOrEmpty() || chapter.pages.any { it.isBlank() }) {
                                context.longToast(R.string.list_chapters_open_not_exists)
                            } else {
                                // go to viewer
                                /*
                                * act.startActivity<ViewerActivity>(
                            "chapter" to chapter,
                            "is" to act.manga.isAlternativeSort
                        )
                                * */
                            }
                        }
                    } else viewModel.onSelectItem(index)
                },
                onLongClick = {
                    log = "longClick"
                    viewModel.onSelectItem(index)
                }
            ),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            // name
            Text(
                chapter.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp),
            ) {
                // downloadIndicator
                AnimatedVisibility(downloadIndicator) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(3.dp))

                // status
                AnimatedVisibility(downloadIndicator.not()) {
                    Text(
                        stringResource(
                            R.string.list_chapters_read,
                            chapter.progress,
                            chapter.pages.size,
                            countPagesInMemory
                        ),
                        style = MaterialTheme.typography.body2,
                    )
                }

                AnimatedVisibility(loadingIndicator) {
                    Text(
                        stringResource(R.string.list_chapters_download_progress, downloadPercent),
                        style = MaterialTheme.typography.body2,
                    )
                }

                AnimatedVisibility(queueIndicator) {
                    Text(
                        stringResource(R.string.list_chapters_queue),
                        style = MaterialTheme.typography.body2,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // deleteIndicator
                AnimatedVisibility(deleteIndicator && downloadIndicator.not()) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "indicator for available deleting"
                    )
                }

                // Date
                Text(
                    chapter.date,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.alignByBaseline(),
                )
            }
        }

        // download button
        AnimatedVisibility(downloadIndicator.not()) {
            IconButton(
                onClick = {
                    DownloadService.addOrStart(context, chapter.toDownloadItem())
                },
            ) {
                Icon(Icons.Default.Download, contentDescription = "download button")
            }
        }

        // cancel button
        AnimatedVisibility(downloadIndicator) {
            IconButton(
                onClick = {
                    DownloadService.pause(context, chapter.toDownloadItem())
                },
            ) {
                Icon(Icons.Default.Close, contentDescription = "cancel download button")
            }
        }
    }

    LaunchedEffect(chapter) {
        withContext(Dispatchers.Default) {
            countPagesInMemory = chapter.countPages
        }
    }
}