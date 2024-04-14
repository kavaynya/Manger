package com.san.kir.core.compose.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.Serializable

@Serializable
data class SharedParams(
    private var boundsContainer: RectContainer,
    val cornerRadius: Float,
    val fromCenter: Boolean
) {
    var bounds: Rect
        get() = boundsContainer.toRect()
        set(value) {
            boundsContainer = value.toContainer()
        }


}

@Serializable
data class RectContainer(val b: Float, val l: Float, val r: Float, val t: Float) {
    companion object {
        val ZERO = RectContainer(0f, 0f, 0f, 0f)
    }
}

fun Rect.toContainer() = RectContainer(bottom, left, right, top)
fun RectContainer.toRect() = Rect(l, t, r, b)

@Composable
fun rememberSharedParams(
    bounds: Rect = Rect.Zero,
    cornerRadius: Float = 0f,
    fromCenter: Boolean = false
): SharedParams {
    val radius = with(LocalDensity.current) { Dp(cornerRadius).toPx() }
    return remember { SharedParams(bounds.toContainer(), radius, fromCenter) }
}

fun Modifier.saveParams(params: SharedParams): Modifier {
    return onGloballyPositioned {
        params.bounds = it.boundsInWindow()
    }
}
