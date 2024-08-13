package com.san.kir.chapters.ui.download

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.san.kir.chapters.R
import com.san.kir.chapters.utils.BarPadding
import com.san.kir.chapters.utils.ChapterName
import com.san.kir.chapters.utils.Download
import com.san.kir.chapters.utils.DownloadButton
import com.san.kir.chapters.utils.MenuItem
import com.san.kir.core.compose.CircleLogo
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.IconSize
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.animation.FromBottomToBottomAnimContent
import com.san.kir.core.compose.animation.FromTopToTopAnimContent
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.internet.NetworkState
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.DownloadItem
import com.san.kir.data.models.utils.DownloadState
import kotlinx.coroutines.launch


private val InnerBarPadding = Dimensions.middle
private val BarHeightWithPadding = IconSize.height + BarPadding + InnerBarPadding * 2

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun DownloadsScreen(navigateUp: () -> Unit) {
    val holder: DownloadsStateHolder = stateHolder { DownloadsViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val canScrollUp = remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 5 } }


    ScreenList(
        additionalPadding = Dimensions.zero,
        contentPadding = bottomInsetsPadding(bottom = BarHeightWithPadding),
        state = lazyListState,
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.downloader_format, state.loadingCount),
            subtitle = stringResource(
                R.string.subtitle_format, state.stoppedCount, state.completedCount
            ),
            actions = {
                StartAnimatedVisibility(visible = canScrollUp.value) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .horizontalInsetsPadding(right = Dimensions.half)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardDoubleArrowUp,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { scope.launch { lazyListState.animateScrollToItem(0) } }
                                .padding(Dimensions.half)
                        )
                    }
                }
            }
        ),
        bottomContent = { BottomManagerBar(state.network, sendAction) }
    ) {

        state.items.forEach { group ->
            stickyHeader(key = group.groupName) {
                Text(
                    text = stringResource(group.groupName),
                    modifier = Modifier
                        .padding(top = Dimensions.default, bottom = Dimensions.quarter)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(0, 30, 30, 0)
                        )
                        .padding(horizontal = Dimensions.half, vertical = Dimensions.quarter),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            items(items = group.items, key = { it.id }) { item ->
                ItemView(item, sendAction)
            }
        }
    }
}

@Composable
private fun BottomManagerBar(state: NetworkState, sendEvent: (DownloadsAction) -> Unit) {
    FromBottomToBottomAnimContent(targetState = state) {
        when (it) {
            NetworkState.NOT_WIFI -> {
                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomInsetsPadding()
                ) {
                    Text(stringResource(com.san.kir.background.R.string.wifi_off))
                }
            }

            NetworkState.NOT_CELLULAR -> {
                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomInsetsPadding()
                ) {
                    Text(stringResource(com.san.kir.background.R.string.internet_off))
                }
            }

            NetworkState.OK -> {
                var showMenu by remember { mutableStateOf(false) }

                // Массовое управление загрузками
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomInsetsPadding()
                ) {
                    FullWeightSpacer()

                    // Кнопка включения отображения всех глав
                    IconButton(onClick = { sendEvent(DownloadsAction.StartAll) }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    }

                    // Кнопка паузы
                    IconButton(
                        onClick = { sendEvent(DownloadsAction.StopAll) },
                        modifier = Modifier.padding(start = Dimensions.default)
                    ) { Icon(Icons.Filled.Stop, contentDescription = null) }

                    FullWeightSpacer()

                    // Кнопка очистки списка загрузок
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        ClearDownloadsMenu(showMenu, { showMenu = false }, sendEvent)
                    }

                    FullWeightSpacer()
                }
            }
        }
    }
}

@Composable
private fun ClearDownloadsMenu(
    expanded: Boolean,
    onClose: () -> Unit,
    sendAction: (DownloadsAction) -> Unit,
) {
    TopAnimatedVisibility(
        expanded,
        modifier = Modifier.bottomInsetsPadding(bottom = BarHeightWithPadding + Dimensions.half)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Dimensions.default, vertical = Dimensions.half)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(Dimensions.default)
                )
                .padding(horizontal = Dimensions.half)
                .padding(bottom = Dimensions.default)
        ) {
            MenuItem(R.string.clean_completed) {
                sendAction(DownloadsAction.CompletedClear)
                onClose()
            }
            MenuItem(R.string.clean_paused) {
                sendAction(DownloadsAction.PausedClear)
                onClose()
            }
            MenuItem(R.string.clean_with_error) {
                sendAction(DownloadsAction.ErrorClear)
                onClose()
            }
            MenuItem(R.string.clean_all) {
                sendAction(DownloadsAction.ClearAll)
                onClose()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.ItemView(
    item: DownloadItem,
    sendAction: (DownloadsAction) -> Unit,
) {
    val totalPages by remember { derivedStateOf { item.pages.size } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement()
            .horizontalInsetsPadding(horizontal = Dimensions.default)
            .padding(top = Dimensions.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            CircleLogo(logoUrl = item.logo)

            if (item.status == DownloadState.ERROR)
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier.size(Dimensions.Image.small)
                ) {
                    Icon(
                        painterResource(R.drawable.unknown),
                        contentDescription = "unknown",
                        modifier = Modifier.size(Dimensions.default)
                    )
                }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = Dimensions.half),
        ) {
            ProvideTextStyle(MaterialTheme.typography.titleSmall) {
                if (item.needShowMangaName) MangaName(item.manga)

                ChapterName(item.name)

                StatusText(
                    state = item.status,
                    size = item.size,
                    time = item.time(),
                    downloadPages = item.downloadPages,
                    totalPages = totalPages
                )
            }

            ProgressIndicator(state = item.status, progress = item.progress)
        }

        DownloadButton(state = item.status) {
            when (it) {
                Download.START -> sendAction(DownloadsAction.StartDownload(item.id))
                Download.STOP -> sendAction(DownloadsAction.StopDownload(item.id))
            }
        }
    }
}

@Composable
private fun ProgressIndicator(state: DownloadState, progress: Float) {
    FromTopToTopAnimContent(targetState = state) {
        when (it) {
            DownloadState.QUEUED -> {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = Dimensions.quarter)
                        .height(Dimensions.quarter)
                        .fillMaxWidth(),
                    strokeCap = StrokeCap.Round,
                )
            }

            DownloadState.LOADING -> {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .padding(top = Dimensions.quarter)
                        .height(Dimensions.quarter)
                        .fillMaxWidth(),
                    strokeCap = StrokeCap.Round,
                )
            }

            else -> {
            }
        }
    }
}

@Composable
private fun StatusText(
    state: DownloadState,
    size: String,
    time: String,
    downloadPages: Int,
    totalPages: Int,
) {
    FromTopToTopAnimContent(targetState = state) {
        when (it) {
            DownloadState.COMPLETED ->
                Text(stringResource(R.string.download_final_format, size, time))

            else ->
                Text(
                    stringResource(
                        R.string.concat_format,
                        stringResource(
                            R.string.download_progress_format, downloadPages, totalPages
                        ),
                        stringResource(R.string.size_format, size)
                    )
                )
        }
    }
}

@Composable
private fun BottomInfoBar(@StringRes textId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseSurface,
                shape = RoundedCornerShape(
                    topStart = Dimensions.default,
                    topEnd = Dimensions.default,
                )
            ),
    ) {
        Text(
            text = stringResource(textId),
            modifier = Modifier
                .bottomInsetsPadding()
                .padding(Dimensions.default),
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
private fun MangaName(manga: String) {
    Text(
        manga,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
