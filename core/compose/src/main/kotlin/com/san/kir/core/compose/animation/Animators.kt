package com.san.kir.core.compose.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.isFinished
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
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
import androidx.compose.ui.unit.Dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimator
import kotlin.math.hypot

const val ANIMATION_DURATION = 600

fun shapeAnimator(
    params: SharedParams,
    maxFactorForItem: Float = 0.2f,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION)
) =
    if (params.fromCenter) circleShapeAnimator(params, animationSpec)
    else itemShapeAnimator(params, maxFactorForItem, animationSpec)

fun circleShapeAnimator(
    params: SharedParams,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION)
) = shapeAnimator { factor ->
    GenericShape { size, _ ->
        addOval(
            Rect(
                center = params.bounds.center,
                radius = hypot(size.width, size.height) * factor
            )
        )
    }
}

fun itemShapeAnimator(
    params: SharedParams,
    maxFactorForItem: Float = 0.2f,
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION)
) = shapeAnimator(animationSpec) { factor ->
    GenericShape { size, _ ->
        if (factor <= maxFactorForItem) {
            addRoundRect(
                RoundRect(
                    lerp(params.bounds.rectCenter(), params.bounds, factor / maxFactorForItem),
                    params.cornerRadius,
                    params.cornerRadius,
                )
            )
            return@GenericShape
        }

        val currentFactor = (factor - maxFactorForItem) / (1f - maxFactorForItem)
        val radius = params.cornerRadius * (1f - currentFactor)
        val lerp = lerp(params.bounds, size.toRect(), currentFactor)
        if (radius > 0f) {
            addRoundRect(RoundRect(lerp, radius, radius))
        } else {
            addRect(lerp)
        }
    }
}

fun horizontalSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(ANIMATION_DURATION),
    toRight: Boolean = true
) = stackAnimator(animationSpec) { factor, direction, content ->
    content(Modifier.offsetXFactor(if (toRight) -factor else factor))
}

fun verticalSlide(
    animationSpec: FiniteAnimationSpec<Float> = tween(ANIMATION_DURATION),
    toBottom: Boolean = true
) = stackAnimator(animationSpec) { factor, direction, content ->
    content(Modifier.offsetYFactor(if (toBottom) -factor else factor))
}

fun shapeAnimator(
    animationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION),
    shape: @Composable (factor: Float) -> Shape
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

    val factor = when (direction) {
        Direction.ENTER_FRONT -> 1f - animationState.value
        Direction.EXIT_FRONT -> animationState.value
        Direction.ENTER_BACK -> 1f - animationState.value
        Direction.EXIT_BACK -> animationState.value
    }

    val then = if (direction.isFront) {
        Modifier.clip(shape(factor))
    } else {
        Modifier.blur(
            radius = Dp(5f * factor),
            edgeTreatment = BlurredEdgeTreatment.Unbounded
        )
    }

    content(then)
}

fun Modifier.offsetXFactor(factor: Float): Modifier {
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        layout(width, placeable.height) {
            placeable.placeRelative((width * factor).toInt(), 0, 0f)
        }
    }
}

fun Modifier.offsetYFactor(factor: Float): Modifier {
    return layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val height = placeable.height
        layout(placeable.width, height) {
            placeable.placeRelative(0, (height * factor).toInt(), 0f)
        }
    }
}

private fun Rect.rectCenter(): Rect {
    val x = left + width / 2f
    val y = top + height / 2f
    return Rect(x, y, x, y)
}
