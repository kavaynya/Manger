package com.san.kir.chapters.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.BarContainerColor
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.Saver
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.maxDistanceIn

internal val DefaultRoundedShape = RoundedCornerShape(50)
internal val SelectedBarPadding = Dimensions.half
internal val BarPadding = Dimensions.default

internal val SelectedBarColor: Color
    @Composable get() = BarContainerColor.copy(alpha = 0.95f)

internal val SelectedItemContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer

internal val ReadingItemContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)

@Composable
internal fun selectionModeColor(state: Boolean): Color {
    return if (state) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
}

@Composable
internal fun SelectableReadableItemRow(
    isRead: Boolean,
    selected: Boolean,
    screenWidth: MutableFloatState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val selectedColor = SelectedItemContainerColor
    val readingColor = ReadingItemContainerColor

    var itemSize by remember { mutableStateOf(Size.Zero) }
    var lastPressPosition by rememberSaveable(stateSaver = Offset.Saver) { mutableStateOf(Offset.Zero) }

    val backgroundSize = rememberFloatAnimatable(if (isRead) screenWidth.floatValue else 0f)
    LaunchedEffect(isRead) {
        if (isRead) {
            backgroundSize.animateTo(screenWidth.floatValue)
        } else {
            backgroundSize.animateTo(0f)
        }
    }

    val selectedRadius =
        rememberFloatAnimatable(if (selected) lastPressPosition.maxDistanceIn(itemSize) else 0f)

    LaunchedEffect(selected) {
        if (selected) {
            selectedRadius.animateTo(lastPressPosition.maxDistanceIn(itemSize))
        } else {
            selectedRadius.animateTo(0f)
        }
    }

    LaunchedEffect(Unit) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press) {
                lastPressPosition = interaction.pressPosition
            }
        }
    }

    Row(
        modifier = Modifier
            .drawWithCache {
                onDrawBehind {
                    clipRect {}
                    itemSize = size
                    drawRect(
                        color = readingColor,
                        size = size.copy(backgroundSize.value)
                    )
                    drawCircle(
                        color = selectedColor,
                        radius = selectedRadius.value,
                        center = lastPressPosition
                    )
                }
            }
            .fillMaxWidth()
            .horizontalInsetsPadding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
internal fun MenuItem(
    id: Int,
    param: String? = null,
    onClick: () -> Unit
) {

    Text(
        text = if (param == null) stringResource(id) else stringResource(id, param),
        modifier = Modifier
            .clip(DefaultRoundedShape)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = Dimensions.half, vertical = Dimensions.middle),
        color = MaterialTheme.colorScheme.onSurface
    )
}

