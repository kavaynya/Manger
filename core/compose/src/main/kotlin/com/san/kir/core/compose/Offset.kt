package com.san.kir.core.compose

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import kotlin.math.sqrt

internal fun Rect.corners() = listOf(topLeft, bottomLeft, topRight, bottomRight)
internal fun Size.corners() = toRect().corners()

public fun Offset.maxDistanceIn(size: Size): Float {
    return sqrt(size.corners().map { it - this }.maxOf { it.getDistanceSquared() })
}

public val Offset.Companion.Saver: Saver<Offset, *>
    get() = Saver(
        save = { it.x to it.y },
        restore = { Offset(it.first, it.second) },
    )
