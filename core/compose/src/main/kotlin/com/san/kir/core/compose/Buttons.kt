package com.san.kir.core.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams

public val IconSize: DpSize = DpSize(24.dp + Dimensions.default * 2, 24.dp + Dimensions.default * 2)
public val IconButtonPaddings: PaddingValues.Absolute =
    PaddingValues.Absolute(Dimensions.default, Dimensions.half, Dimensions.default, Dimensions.half)
public val FabButtonHeight: Dp = 72.dp

@Composable
public fun <T> LazyRadioGroup(
    state: T,
    onSelected: (T) -> Unit,
    stateList: List<T>,
    textList: List<String>,
) {
    LazyColumn {
        items(stateList.size, key = { it }) { index ->
            val s = stateList[index]
            val text = textList[index]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(s) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = state == s, onClick = { onSelected(s) })
                Text(text, modifier = Modifier.padding(horizontal = Dimensions.half))
            }
        }
    }
}

@Composable
public fun <T> RadioGroup(
    state: T,
    onSelected: (T) -> Unit,
    stateList: List<T>,
    textList: List<String>,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        stateList.zip(textList).forEach { (s, text) ->
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .selectable(selected = state == s, role = Role.RadioButton, onClick = { onSelected(s) })
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.quarter),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = state == s, onClick = { onSelected(s) })
                Text(
                    text,
                    modifier = Modifier.padding(horizontal = Dimensions.half),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
public fun OutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        Modifier
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight
            )
            .padding(ButtonDefaults.ContentPadding)
            .border(
                Dimensions.smallest,
                borderColor,
                RoundedCornerShape(Dimensions.quarter)
            )
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
public fun IconCounterButton(
    icon: ImageVector,
    contentDescription: String?,
    counter: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
) {
    Box {
        IconButton(onClick, modifier.align(Alignment.Center), enabled, colors, interactionSource) {
            Icon(icon, contentDescription)
        }
        Text(
            counter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f), CircleShape)
                .padding(horizontal = Dimensions.quarter, vertical = Dimensions.smallest),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
public fun RotateToggleButton(icon: ImageVector, state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val reverseRotate = animateFloatAsState(if (state) 0f else 540f, label = "")

    Icon(
        icon,
        contentDescription = "",
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .size(IconSize)
            .padding(IconButtonPaddings)
            .graphicsLayer {
                rotationZ = reverseRotate.value
            }
    )
}

@ThemedPreview
@Composable
private fun PreviewRotateToggleButton() {
    ThemedPreviewContainer {
        Column {
            var state by remember { mutableStateOf(false) }
            RotateToggleButton(icon = Icons.AutoMirrored.Filled.Sort, state) { state = !state }
        }
    }
}

@Composable
public fun FabButton(
    modifier: Modifier = Modifier,
    image: ImageVector = Icons.Default.Add,
    onClick: (SharedParams) -> Unit
) {
    val params = rememberSharedParams(fromCenter = true)
    Icon(
        image,
        contentDescription = "",
        modifier = Modifier
            .bottomInsetsPadding(right = Dimensions.default, bottom = Dimensions.default)
            .horizontalInsetsPadding()
            .clip(CircleShape)
            .saveParams(params)
            .clickable { onClick(params) }
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f))
            .padding(Dimensions.default)
            .then(modifier),
        tint = MaterialTheme.colorScheme.onSecondaryContainer
    )
}
