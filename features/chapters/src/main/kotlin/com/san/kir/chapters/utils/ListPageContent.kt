package com.san.kir.chapters.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderDelete
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.san.kir.chapters.R
import com.san.kir.chapters.ui.chapters.BottomSortHelper
import com.san.kir.chapters.ui.chapters.ChaptersAction
import com.san.kir.chapters.ui.chapters.ChaptersEvent
import com.san.kir.chapters.ui.chapters.Filter
import com.san.kir.chapters.ui.chapters.Items
import com.san.kir.chapters.ui.chapters.SelectableItem
import com.san.kir.chapters.ui.chapters.Selection
import com.san.kir.chapters.ui.chapters.SelectionMode
import com.san.kir.core.compose.DefaultBottomBar
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.IconButtonPaddings
import com.san.kir.core.compose.IconSize
import com.san.kir.core.compose.RotateToggleButton
import com.san.kir.core.compose.Saver
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.animation.FromBottomToBottomAnimContent
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberDpAnimatable
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.maxDistanceIn
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.data.models.base.downloadProgress
import com.san.kir.data.models.main.SimplifiedChapter
import com.san.kir.data.models.utils.ChapterFilter
import com.san.kir.data.models.utils.DownloadState

private val SortBarPadding = Dimensions.default

private val SortSelectedContainerColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.inverseSurface

private val SortSelectedContentColor: Color
    @Composable
    get() = contentColorFor(SortSelectedContainerColor)

private val SortBarHeightWithPadding = IconSize.height + SortBarPadding * 4


// Страница со списком и инструментами для манипуляции с ним
@Composable
internal fun ListPageContent(
    chapterFilter: ChapterFilter,
    selectionMode: SelectionMode,
    itemsContent: Items,
    sendAction: (Action) -> Unit,
) {
    val itemsCount by remember { derivedStateOf { itemsContent.count } }
    var screenWidth by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenWidth = it.boundsInWindow().width
            }
    ) {
        if (itemsContent.items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = if (selectionMode.enabled) Dimensions.zero else SortBarHeightWithPadding
                )
            ) {
                itemsIndexed(
                    items = itemsContent.items,
                    key = { _, ch -> ch.chapter.id }
                ) { index, chapter ->
                    val memoryPagesCount = itemsContent.memoryPagesCounts[chapter.chapter.id] ?: 0
                    ItemContent(
                        item = chapter,
                        index = index,
                        selectionEnabled = selectionMode.enabled,
                        memoryPagesCount = memoryPagesCount,
                        screenWidth = screenWidth,
                        sendAction = sendAction
                    )
                }
            }
        }

        TopAnimatedVisibility(
            visible = !selectionMode.enabled,
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            BottomOrderBar(chapterFilter) {
                sendAction(ChaptersAction.ChangeFilter(it))
            }
        }

        StartAnimatedVisibility(
            visible = selectionMode.enabled,
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            SelectionModeBar(
                selectionMode = selectionMode,
                itemsCount = itemsCount,
                sendAction = sendAction
            )
        }
    }
}

// Нижний бар управления сортировкой и фильтрацией списка
@Composable
private fun BottomOrderBar(
    currentFilter: ChapterFilter,
    sendAction: (Filter) -> Unit,
) {
    val selectedColor = MaterialTheme.colorScheme.inverseSurface

    val buttons = remember {
        listOf(
            BottomSortHelper(
                icon = Icons.Default.SelectAll,
                action = { sendAction(Filter.All) },
                checkEnable = { it.isAll }
            ),
            BottomSortHelper(
                icon = Icons.Default.Visibility,
                action = { sendAction(Filter.Read) },
                checkEnable = { it.isRead }
            ),
            BottomSortHelper(
                icon = Icons.Default.VisibilityOff,
                action = { sendAction(Filter.NotRead) },
                checkEnable = { it.isNot }
            )
        )
    }

    val currentButtonIndex = buttons.indexOfFirst { it.checkEnable(currentFilter) }
    val buttonOffset = rememberDpAnimatable(IconSize.width * currentButtonIndex)

    LaunchedEffect(currentButtonIndex) {
        buttonOffset.animateTo(IconSize.width * currentButtonIndex)
    }

    DefaultBottomBar {
        RotateToggleButton(
            icon = Icons.AutoMirrored.Filled.Sort,
            state = currentFilter.isAsc,
            onClick = { sendAction(Filter.Reverse) }
        )
        Spacer(modifier = Modifier.width(SortBarPadding))
        Row(
            modifier = Modifier
                .padding(SortBarPadding)
                .drawBehind {
                    drawRoundRect(
                        color = selectedColor,
                        topLeft = Offset(buttonOffset.value.toPx(), 0f),
                        size = size.copy(size.width / buttons.size),
                        cornerRadius = CornerRadius(size.height / 2)
                    )
                }
        ) {
            buttons.forEachIndexed { index, (icon, action) ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(DefaultRoundedShape)
                        .clickable(onClick = action, enabled = index != currentButtonIndex)
                        .size(IconSize)
                        .padding(IconButtonPaddings),
                    tint =
                    if (index == currentButtonIndex) SortSelectedContentColor
                    else LocalContentColor.current
                )
            }
        }
    }
}

@Composable
internal fun SelectionModeBar(
    selectionMode: SelectionMode,
    itemsCount: Int,
    sendAction: (Action) -> Unit
) {
    val allSelected by remember {
        derivedStateOf { selectionMode.selectionCount == itemsCount }
    }
    val singleSelected by remember {
        derivedStateOf { selectionMode.selectionCount == 1 }
    }

    Row(
        modifier = Modifier
            .bottomInsetsPadding()
            .padding(bottom = Dimensions.default),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .background(SelectedBarColor, DefaultRoundedShape)
                .padding(SelectedBarPadding)
        ) {

            TopAnimatedVisibility(visible = singleSelected) {
                IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.Above)) }) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Select Above")
                }
            }

            BottomAnimatedVisibility(visible = allSelected.not()) {
                IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.All)) }) {
                    Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                }
            }

            BottomAnimatedVisibility(visible = singleSelected) {
                IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.Below)) }) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Select Below")
                }
            }

            IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.Clear)) }) {
                Icon(Icons.Default.Close, contentDescription = "Clear Selection")
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(SelectedBarPadding)
            .background(SelectedBarColor, DefaultRoundedShape)
            .padding(SelectedBarPadding)
    ) {
        IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.Download)) }) {
            Icon(Icons.Default.Download, contentDescription = "Download items")
        }

        BottomAnimatedVisibility(visible = selectionMode.canRemovePages) {
            IconButton(onClick = { sendAction(ReturnEvents(ChaptersEvent.ShowDeleteDialog)) }) {
                Icon(Icons.Default.FolderDelete, contentDescription = "Delete Selection")
            }
        }

        BottomAnimatedVisibility(visible = selectionMode.canSetRead) {
            IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.SetRead(true))) }) {
                Icon(Icons.Default.Visibility, contentDescription = null)
            }
        }

        BottomAnimatedVisibility(visible = selectionMode.canSetUnread) {
            IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.SetRead(false))) }) {
                Icon(Icons.Default.VisibilityOff, contentDescription = null)
            }
        }

        BottomAnimatedVisibility(visible = selectionMode.hasReading) {
            IconButton(onClick = { sendAction(ChaptersAction.WithSelected(Selection.Reset)) }) {
                Icon(Icons.Default.LockReset, contentDescription = null)
            }
        }

        IconButton(onClick = { sendAction(ReturnEvents(ChaptersEvent.ShowFullDeleteDialog)) }) {
            Icon(Icons.Default.DeleteForever, contentDescription = "Download items")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.ItemContent(
    item: SelectableItem,
    index: Int,
    selectionEnabled: Boolean,
    memoryPagesCount: Int,
    screenWidth: Float,
    sendAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    var itemSize by remember { mutableStateOf(Size.Zero) }
    var lastPressPosition by rememberSaveable(stateSaver = Offset.Saver) { mutableStateOf(Offset.Zero) }

    val backgroundSize = rememberFloatAnimatable(if (item.chapter.isRead) screenWidth else 0f)

    LaunchedEffect(item.chapter.isRead) {
        backgroundSize.animateTo(if (item.chapter.isRead) screenWidth else 0f)
    }

    val selectedRadius = rememberFloatAnimatable(
        if (item.selected) lastPressPosition.maxDistanceIn(itemSize) else 0f
    )

    LaunchedEffect(item.selected) {
        selectedRadius.animateTo(
            if (item.selected) lastPressPosition.maxDistanceIn(itemSize) else 0f
        )
    }

    LaunchedEffect(Unit) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                lastPressPosition = interaction.pressPosition
            }
        }

        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press && item.selected.not()) {
                lastPressPosition = interaction.pressPosition
            }
        }
    }
    val readingColor = ReadingItemContainerColor
    val selectedColor = SelectedItemContainerColor

    Row(
        modifier = modifier
            .drawWithCache {
                onDrawBehind {
                    clipRect { }
                    itemSize = size

                    drawRect(
                        color = readingColor,
                        size = size.copy(width = backgroundSize.value)
                    )

                    // Draw the selected circle
                    drawCircle(
                        color = selectedColor,
                        radius = selectedRadius.value,
                        center = lastPressPosition
                    )
                }
            }
            .fillMaxWidth()
            .horizontalInsetsPadding()
            .animateItemPlacement(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val indication = LocalIndication.current
        Column(
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    interactionSource = interactionSource, indication = indication,
                    onLongClick = { sendAction(ChaptersAction.WithSelected(Selection.Change(index))) },
                    onClick = onClickItem(
                        selectionEnabled,
                        item.chapter,
                        { sendAction(ReturnEvents(ChaptersEvent.ToViewer(it))) },
                        { sendAction(ChaptersAction.WithSelected(Selection.Change(index))) }
                    ),
                )
                .padding(start = Dimensions.default, end = Dimensions.half)
                .padding(vertical = Dimensions.half)
        ) {
            ChapterName(item.chapter.name)

            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.quarter)
                ) {
                    StatusText(
                        item.chapter.status,
                        item.chapter.downloadProgress,
                        item.chapter.progress,
                        item.chapter.pages.size,
                        memoryPagesCount
                    )

                    FullWeightSpacer()

                    Text(
                        text = item.chapter.date,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
        }

        StartAnimatedVisibility(visible = !selectionEnabled) {
            DownloadButton(
                state = item.chapter.status,
                sendAction = {
                    when (it) {
                        Download.START -> sendAction(ChaptersAction.StartDownload(item.chapter.id))
                        Download.STOP -> sendAction(ChaptersAction.StopDownload(item.chapter.id))
                    }
                },
            )
        }
    }
}

@Composable
private fun StatusText(
    state: DownloadState,
    downloadProgress: Int,
    progress: Int,
    size: Int,
    localCountPages: Int,
) {
    FromBottomToBottomAnimContent(targetState = state) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (it) {
                DownloadState.LOADING -> {
                    LoadingIndicator()
                    LoadingText(downloadProgress)
                }

                DownloadState.QUEUED -> {
                    LoadingIndicator()
                    WaitingText()
                }

                DownloadState.ERROR,
                DownloadState.PAUSED,
                DownloadState.COMPLETED,
                DownloadState.UNKNOWN,
                -> {
                    Text(
                        stringResource(
                            R.string.reading_progress_format, progress, size, localCountPages
                        ),
                    )

                    BottomAnimatedVisibility(visible = localCountPages > 0) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "indicator for available deleting",
                            modifier = Modifier
                                .padding(end = Dimensions.quarter)
                                .size(Dimensions.default),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSelectedModeOnListPageContent() {
    MaterialTheme {
        ListPageContent(
            chapterFilter = ChapterFilter.ALL_READ_DESC,
            selectionMode = remember {
                SelectionMode(
                    selectionCount = 1,
                    hasReading = false,
                    canSetRead = false,
                    canSetUnread = false,
                    canRemovePages = false
                )
            },
            itemsContent = remember {
                Items(
                    listOf(
                        createTestItem(1, false),
                        createTestItem(2, false),
                        createTestItem(3, false),
                        createTestItem(4, false),
                        createTestItem(5, false)
                    )
                )
            },
            sendAction = { }
        )
    }
}

@Preview
@Composable
private fun PreviewSelectedModeOffListPageContent() {
    MaterialTheme {
        ListPageContent(
            chapterFilter = ChapterFilter.ALL_READ_DESC,
            selectionMode = SelectionMode(
                selectionCount = 0,
                hasReading = false,
                canSetRead = false,
                canSetUnread = false,
                canRemovePages = false
            ),
            itemsContent = Items(
                listOf(
                    createTestItem(1, false),
                    createTestItem(2, true),
                    createTestItem(3, true),
                    createTestItem(4, false),
                    createTestItem(5, false)
                )
            ),
            sendAction = { }
        )
    }
}

private fun createTestItem(
    index: Int,
    selected: Boolean = false,
    progress: Int = 0,
    isRead: Boolean = false,
    downloadState: DownloadState = DownloadState.UNKNOWN,
    downloadPages: Int = 0
): SelectableItem {
    return SelectableItem(
        chapter = SimplifiedChapter(
            id = index.toLong(),
            status = downloadState,
            name = "Тестовая глава $index",
            progress = progress,
            isRead = isRead,
            downloadPages = downloadPages,
            pages = emptyList(),
            manga = "Тестовая манга",
            date = "",
            path = "",
            addedTimestamp = 0L
        ),
        selected = selected
    )
}
