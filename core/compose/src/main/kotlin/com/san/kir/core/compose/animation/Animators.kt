@file:Suppress("NOTHING_TO_INLINE")

package com.san.kir.core.compose.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.isFinished
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimator
import kotlin.math.hypot

const val ANIMATION_DURATION = 600

object EmptyStackAnimator : StackAnimator {

    @Composable
    override fun invoke(
        direction: Direction,
        isInitial: Boolean,
        onFinished: () -> Unit,
        content: @Composable (Modifier) -> Unit,
    ) {
        content(Modifier)

        DisposableEffect(direction, isInitial) {
            onFinished()
            onDispose {}
        }
    }
}

inline fun shapeAnimator(
    params: SharedParams,
    maxFactorForItem: Float = 0.2F,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION),
) =
    if (params.fromCenter) circleShapeAnimator(params, animationSpec)
    else itemShapeAnimator(params, maxFactorForItem, animationSpec)


fun circleShapeAnimator(
    params: SharedParams,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION),
) = shapeAnimator(animationSpec) { factor ->
    GenericShape { size, _ ->
        addOval(Rect(params.bounds.center, hypot(size.width, size.height) * factor))
    }
}

fun itemShapeAnimator(
    params: SharedParams,
    maxFactorForItem: Float = 0.2F,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION),
) = shapeAnimator(animationSpec) { factor ->
    val bounds = params.bounds
    GenericShape { size, _ ->
        if (factor <= maxFactorForItem) {
            addRoundRect(
                RoundRect(
                    lerp(bounds.rectCenter, bounds, factor / maxFactorForItem),
                    params.cornerRadius, params.cornerRadius
                )
            )
        } else {
            val currentFactor = (factor - maxFactorForItem) / (1f - maxFactorForItem)
            val radius = params.cornerRadius * (1f - currentFactor)

            if (radius > 0)
                addRoundRect(
                    RoundRect(
                        lerp(bounds, size.toRect(), currentFactor),
                        radius, radius
                    )
                )
            else addRect(lerp(bounds, size.toRect(), currentFactor))
        }
    }
}

fun horizontalSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(ANIMATION_DURATION),
    toRight: Boolean = true,
): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, _, content ->
        content(Modifier.offsetXFactor(factor = if (toRight) -factor else factor))
    }

fun verticalSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(ANIMATION_DURATION),
    toBottom: Boolean = true,
): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, _, content ->
        content(Modifier.offsetYFactor(factor = if (toBottom) -factor else factor))
    }

inline fun shapeAnimator(
    animationSpec: AnimationSpec<Float>,
    crossinline shape: @Composable (factor: Float) -> Shape,
) = StackAnimator { direction, isInitial, onFinished, content ->
    val animationState = remember(direction, isInitial) {
        AnimationState(initialValue = if (isInitial) 0F else 1F)
    }

    LaunchedEffect(animationState) {
        animationState.animateTo(
            targetValue = 0F,
            animationSpec = animationSpec,
            sequentialAnimation = !animationState.isFinished,
        )

        onFinished()
    }

    val factor =
        when (direction) {
            Direction.ENTER_FRONT -> 1F - animationState.value
            Direction.EXIT_FRONT -> animationState.value
            Direction.EXIT_BACK -> 1F - animationState.value
            Direction.ENTER_BACK -> animationState.value
        }

    content(
        if (direction.isFront) Modifier.then(Modifier.clip(shape(factor)))
        else Modifier.then(Modifier.blur((factor * 5f).dp, BlurredEdgeTreatment.Unbounded))
    )
}

private fun Modifier.offsetXFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
        }
    }

private fun Modifier.offsetYFactor(factor: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = (placeable.height.toFloat() * factor).toInt())
        }
    }

private val Rect.rectCenter: Rect
    get() {
        val x = left + width / 2.0f
        val y = top + height / 2.0f
        return Rect(x, y, x, y)
    }
