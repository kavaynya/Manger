package com.san.kir.chapters.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.san.kir.chapters.ui.latest.DateContainer
import com.san.kir.core.compose.BarContainerColor
import com.san.kir.core.compose.endInsets
import com.san.kir.core.compose.endInsetsPadding

private const val DateType = "Date"
internal const val MangaType = "Manga"

private val DateBarHeight = 40.dp
private val DateBarShape = RoundedCornerShape(bottomStartPercent = 40)
private val DateShape = RoundedCornerShape(topStartPercent = 40, bottomStartPercent = 40)
private val DateBarHorizontalPadding = 12.dp

private val dateTextStyle: TextStyle
    @Composable
    get() = MaterialTheme.typography.titleMedium

internal class DateState(private val height: Float) {
    private val _current = mutableStateOf("")
    private val _hidden = mutableStateOf("")
    private val _alpha = mutableFloatStateOf(1.0f)
    private val _offset = mutableFloatStateOf(0.0f)
    private val _maxWidth = mutableStateOf(0.dp)

    val current: String
        get() = _current.value

    val hidden: String
        get() = _hidden.value

    val alpha: Float
        get() = _alpha.floatValue

    val offset: Float
        get() = _offset.floatValue

    val maxWidth: Dp
        get() = _maxWidth.value

    fun update(offset: Float, date: String, prevDate: String?) {
        _alpha.floatValue = offset / height
        _offset.floatValue = offset
        _current.value = date
        if (prevDate != null) {
            _hidden.value = prevDate
        }
    }

    fun reset(date: String?) {
        _alpha.floatValue = 0.0f
        _offset.floatValue = 0.0f
        if (date != null) {
            _current.value = date
        }
    }

    fun updateWidth(width: Dp) {
        _maxWidth.value = width
    }
}


@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.date(
    date: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    item(key = date, contentType = DateType) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = modifier
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                    .background(color = BarContainerColor, shape = DateShape)
                    .height(DateBarHeight)
                    .endInsetsPadding(horizontal = DateBarHorizontalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = date, style = dateTextStyle)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DateHeader(
    lazyListState: LazyListState,
    itemsState: State<List<DateContainer>>,
    onClick: (List<Long>) -> Unit,
    onLongClick: (List<Long>) -> Unit
) {
    val density = LocalDensity.current
    val dateHeight = density.run { DateBarHeight.toPx() }
    val dateState = remember { DateState(dateHeight) }
    val textMeasurer = rememberTextMeasurer()
    val textStyle = dateTextStyle
    val items by itemsState

    LaunchedEffect(items) {
        val datesWithIndex = mutableMapOf<Int, String>()
        var index = 0
        if (items.isEmpty()) return@LaunchedEffect

        for (container in items) {
            datesWithIndex[index] = container.date
            index += container.chaptersCount + container.mangas.size + 1
        }

        val hidedDates = items.map { it.date }
        val maxWidth = hidedDates.maxOf {
            textMeasurer.measure(text = it, style = textStyle, density = density).size.width
        }
        dateState.updateWidth(with(density) { maxWidth.toDp() });
        dateState.reset(hidedDates.firstOrNull())

        fun findPreviousDate(list: List<String>, date: String): String = list.getOrNull(list.indexOf(date) - 1) ?: ""

        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }.collect { infos ->
            val currentInfo = infos.firstOrNull { it.contentType == DateType && it.offset >= 0 }
            if (currentInfo == null) {
                dateState.reset(hidedDates.lastOrNull())
            } else {
                val date = currentInfo.key.toString()
                val offset = currentInfo.offset.toFloat()
                if (offset in 0.0f..dateHeight) {
                    dateState.update(offset, date, findPreviousDate(hidedDates, date))
                } else {
                    dateState.reset(findPreviousDate(hidedDates, date))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {

        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        items.firstOrNull { it.date == dateState.current }?.let { onClick(it.chaptersIds) }
                    },
                    onLongClick = {
                        items.firstOrNull { it.date == dateState.current }?.let { onLongClick(it.chaptersIds) }
                    }
                )
                .background(BarContainerColor, DateBarShape)
                .height(DateBarHeight)
                .width(
                    dateState.maxWidth
                            + DateBarHorizontalPadding * 2
                            + endInsets().asPaddingValues().calculateEndPadding(LayoutDirection.Ltr)
                )
                .clip(DateShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dateState.hidden,
                modifier = Modifier
                    .endInsetsPadding(horizontal = DateBarHorizontalPadding)
                    .graphicsLayer {
                        alpha = dateState.alpha
                        translationY = dateState.offset - dateHeight
                    },
                style = textStyle
            )
            Text(
                text = dateState.current,
                modifier = Modifier
                    .endInsetsPadding(horizontal = DateBarHorizontalPadding)
                    .graphicsLayer {
                        alpha = 1.0f - dateState.alpha
                        translationY = dateState.offset
                    },
                style = textStyle
            )
        }
    }
}
