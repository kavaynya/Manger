package com.san.kir.core.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize


internal val LocalContainerColor = compositionLocalOf { Color.White }
private val HBarHeight = 50.dp
private val SVBarHeight = 90.dp
private val SelectorThickness = 3.dp
private val SelectorRadius = HBarHeight / 2 - SelectorThickness / 2

@Composable
public fun rememberColorPickerState(initialColor: Color): ColorPickerState =
    rememberSaveable(saver = ColorPickerState.Saver) { ColorPickerState(initialColor) }

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
public fun ColorPicker(state: ColorPickerState, modifier: Modifier = Modifier) {
    Column(modifier) {
        val circleColor = MaterialTheme.colorScheme.onSurface

        BoxWithConstraints(
            modifier = Modifier
                .padding(HBarHeight / 2)
                .padding(top = HBarHeight / 2 + Dimensions.half, bottom = HBarHeight / 2)
                .fillMaxWidth()
                .height(SVBarHeight)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        state.updateSVPosition(change.position, size)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        state.updateSVPosition(it, size)
                    }
                }
        ) {
            state.svSize = IntSize(constraints.maxWidth, constraints.maxHeight).toSize()

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Dimensions.half))
            ) {
                drawImage(state.svBar)
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = circleColor,
                    radius = SelectorRadius.toPx(),
                    center = state.svPosition,
                    style = Stroke(SelectorThickness.toPx()),
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .padding(horizontal = HBarHeight / 2, vertical = Dimensions.default)
                .fillMaxWidth()
                .height(HBarHeight)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        state.updateHPosition(change.position.x, size.width)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        state.updateHPosition(it.x, size.width)
                    }
                }
        ) {
            state.hSize = IntSize(constraints.maxWidth, constraints.maxHeight)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50))
            ) {
                drawImage(state.hBar)
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = circleColor,
                    radius = SelectorRadius.toPx(),
                    center = Offset(state.hPosition, size.height / 2),
                    style = Stroke(SelectorThickness.toPx()),
                )
            }
        }
    }
}
