package com.san.kir.core.compose

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

fun Modifier.squareMaxSize() = this.then(SquareMaxSizeModifier())

class SquareMaxSizeModifier : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val minWidth: Int = constraints.minWidth
        val maxWidth: Int = constraints.maxWidth

        val placeable = measurable.measure(
            Constraints(minWidth, maxWidth, minWidth, maxWidth)
        )

        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

}

fun Modifier.holdPress(onDown: () -> Unit, onUp: () -> Unit) = then(
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            onDown()

            do {
                val event = awaitPointerEvent()
            } while (event.changes.any { it.pressed })

            onUp()
        }
    }
)
