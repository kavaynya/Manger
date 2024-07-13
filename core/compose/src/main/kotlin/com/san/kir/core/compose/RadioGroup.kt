package com.san.kir.core.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.sumOf

private val SortItemHeight = 48.dp
private val ItemHorizontalPadding = Dimensions.default
private fun <T> animationSpec() = spring<T>(DampingRatioLowBouncy, 800f)
private val sortSelectedContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.primary

internal interface IDataHelper<ValueT> {
    val value: ValueT
}

public data class DataIconHelper<ValueT>(val content: ImageVector, override val value: ValueT) :
    IDataHelper<ValueT>

public data class DataTextHelper<ValueT>(val title: Int, override val value: ValueT) :
    IDataHelper<ValueT>

internal interface IHandledDataHelper<ValueT> {
    val value: ValueT
    val width: Float
}

internal class HandledDataTextHelper<ValueT>(
    val title: String,
    override val value: ValueT,
    override val width: Float
) : IHandledDataHelper<ValueT>

internal class HandledDataIconHelper<ValueT>(
    val content: ImageVector,
    override val value: ValueT,
    override val width: Float
) : IHandledDataHelper<ValueT>


@Composable
public fun <ValueT> VerticalRadioGroup(
    dataHelpers: List<DataTextHelper<ValueT>>,
    initialValue: ValueT,
    onChange: (ValueT) -> Unit
) {
    val selectedContainerColor = sortSelectedContainerColor
    val currentButtonIndex = dataHelpers.indexOfFirst { it.value == initialValue }
    val buttonOffset = rememberFloatAnimatable(currentButtonIndex.toFloat())

    LaunchedEffect(currentButtonIndex) {
        buttonOffset.animateTo(currentButtonIndex.toFloat(), animationSpec())
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(1.dp))
            .selectableGroup()
            .width(IntrinsicSize.Max)
            .drawBehind {
                if (currentButtonIndex >= 0) {
                    val itemHeight = size.height / dataHelpers.size
                    drawRoundRect(
                        color = selectedContainerColor,
                        topLeft = Offset(0f, buttonOffset.value * itemHeight),
                        size = size.copy(height = itemHeight),
                        cornerRadius = CornerRadius(itemHeight / 2)
                    )
                }
            }
    ) {
        dataHelpers.forEach { (title, value) ->
            val textColor by animateColorAsState(
                if (initialValue == value) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }, label = ""
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .selectable(
                        selected = initialValue == value,
                        enabled = false,
                        role = Role.RadioButton,
                        onClick = { onChange(value) }
                    )
                    .fillMaxWidth()
                    .height(SortItemHeight)
                    .padding(horizontal = ItemHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(title),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
public fun <ValueT, DataHelper : IDataHelper<out ValueT>, HandledDataHelper : IHandledDataHelper<ValueT>> HorizontalRadioGroup(
    dataHelpers: List<DataHelper>,
    initialValue: ValueT,
    onChange: (ValueT) -> Unit,
    dataHandler: (DataHelper, Float) -> HandledDataHelper,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(HandledDataHelper) -> Unit,
) {
    val selectedContainerColor = sortSelectedContainerColor
    val itemHorizontalPadding = with(LocalDensity.current) { ItemHorizontalPadding.toPx() }
    val handledData = remember { mutableStateListOf<HandledDataHelper>() }
    val buttonOffset = rememberFloatAnimatable(0f)
    val buttonWidth = rememberFloatAnimatable(1f)
    val scrollState = rememberScrollState()

    LaunchedEffect(initialValue) {
        if (handledData.isEmpty()) {
            dataHelpers.mapTo(handledData) { dataHandler(it, itemHorizontalPadding) }
        }
        val currentButtonIndex = handledData.indexOfFirst { it.value == initialValue }
        defaultLaunch {
            buttonOffset.animateTo(handledData.take(currentButtonIndex).sumOf { it.width })
        }
        defaultLaunch {
            buttonWidth.animateTo(handledData.getOrNull(currentButtonIndex)?.width ?: 1f)
        }
        defaultLaunch {
            scrollState.animateScrollTo(scrollState.maxValue / (handledData.size - 1) * currentButtonIndex)
        }
    }

    Row(
        modifier = modifier
            .selectableGroup()
            .width(IntrinsicSize.Max)
            .horizontalScroll(scrollState)
            .drawBehind {
                drawRoundRect(
                    color = selectedContainerColor,
                    topLeft = Offset(buttonOffset.value, 0f),
                    size = size.copy(width = buttonWidth.value),
                    cornerRadius = CornerRadius(buttonWidth.value / 2)
                )
            }
    ) {
        handledData.forEach { helper ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .selectable(
                        selected = initialValue == helper.value,
                        role = Role.RadioButton,
                        onClick = { onChange(helper.value) }
                    )
                    .height(SortItemHeight)
                    .padding(horizontal = ItemHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                content(helper)
            }
        }
    }
}

@Composable
public fun <ValueT> HorizontalTextRadioGroup(
    dataHelpers: List<DataTextHelper<ValueT>>,
    initialValue: ValueT,
    modifier: Modifier = Modifier,
    onChange: (ValueT) -> Unit,
) {
    val context = LocalContext.current
    val textMeasure = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodyLarge

    HorizontalRadioGroup(
        dataHelpers = dataHelpers,
        initialValue = initialValue,
        onChange = onChange,
        dataHandler = { helper, padding ->
            val title = context.getString(helper.title)
            val measure = textMeasure.measure(title, style = textStyle)
            HandledDataTextHelper(
                title = title,
                value = helper.value,
                width = measure.size.width + 2 * padding
            )
        },
        modifier = modifier
    ) { helper ->
        val textColor by animateColorAsState(
            if (initialValue == helper.value) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onPrimary, label = ""
        )
        Text(text = helper.title, color = textColor)
    }
}

@Composable
public fun <ValueT> HorizontalIconRadioGroup(
    dataHelpers: List<DataIconHelper<out ValueT>>,
    initialValue: ValueT,
    modifier: Modifier = Modifier,
    onChange: (ValueT) -> Unit,
) {
    val iconWidth = with(LocalDensity.current) { 24.dp.toPx() }
    HorizontalRadioGroup(
        dataHelpers = dataHelpers,
        initialValue = initialValue,
        onChange = onChange,
        dataHandler = { helper, padding ->
            HandledDataIconHelper(
                content = helper.content,
                value = helper.value,
                width = iconWidth + 2 * padding
            )
        },
        modifier = modifier
    ) { helper ->
        val tint by animateColorAsState(
            if (initialValue == helper.value) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onPrimary, label = ""
        )
        Icon(helper.content, "", tint = tint)
    }
}
