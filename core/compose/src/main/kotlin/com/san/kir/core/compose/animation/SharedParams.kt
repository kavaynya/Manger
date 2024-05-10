@file:Suppress("NOTHING_TO_INLINE")

package com.san.kir.core.compose.animation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedParams(
    private var boundsContainer: RectContainer = RectContainer.Zero,
    var cornerRadius: Float = 0F,
    var fromCenter: Boolean = false
) : Parcelable {
    var bounds: Rect
        get() = boundsContainer.toRect()
        set(value) {
            boundsContainer = value.toContainer()
        }
}

@Parcelize
data class RectContainer(val l: Float, val t: Float, val r: Float, val b: Float) : Parcelable {
    fun toRect() = Rect(l, t, r, b)

    companion object {
        val Zero = RectContainer(0.0f, 0.0f, 0.0f, 0.0f)
    }
}

fun Rect.toContainer() = RectContainer(left, top, right, bottom)

@Composable
fun rememberSharedParams(
    bounds: Rect = Rect.Zero,
    cornerRadius: Dp = 0.dp,
    fromCenter: Boolean = false
): SharedParams {
    val radiusInPx = with(LocalDensity.current) { cornerRadius.toPx() }
    return remember { SharedParams(bounds.toContainer(), radiusInPx, fromCenter) }
}

@Stable
fun Modifier.saveParams(params: SharedParams) =
    onGloballyPositioned { params.bounds = it.boundsInWindow() }
