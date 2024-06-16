@file:OptIn(ExperimentalAnimationApi::class)

package com.san.kir.core.compose.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

private const val DEFAULT_DURATION = 300

@Composable
public fun <S> FromTopToTopAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { fullHeight -> -fullHeight }
            ) togetherWith slideOutVertically(
                animationSpec = tween(200),
                targetOffsetY = { fullHeight -> -fullHeight }
            )
        },
        label = "FromTopToTopAnimContent"
    )
}

@Composable
public fun <S> FromTopToBottomAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { fullHeight -> -fullHeight }
            ) togetherWith slideOutVertically(
                animationSpec = tween(400),
                targetOffsetY = { fullHeight -> fullHeight }
            )
        },
        label = "FromTopToBottomAnimContent"
    )
}

@Composable
public fun <S> FromBottomToBottomAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { fullHeight -> fullHeight }
            ) togetherWith slideOutVertically(
                animationSpec = tween(200),
                targetOffsetY = { fullHeight -> fullHeight }
            )
        },
        label = "FromBottomToBottomAnimContent"
    )
}

@Composable
public fun <S> FromBottomToTopAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(400),
                initialOffsetY = { fullHeight -> fullHeight }
            ) togetherWith slideOutVertically(
                animationSpec = tween(400),
                targetOffsetY = { fullHeight -> -fullHeight }
            )
        },
        label = "FromBottomToTopAnimContent"
    )
}

@Composable
public fun <S> FromStartToStartAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(DEFAULT_DURATION),
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = animationSpec,
                initialOffsetX = { fullWidth -> -fullWidth }
            ) togetherWith slideOutHorizontally(
                animationSpec = animationSpec,
                targetOffsetX = { fullWidth -> -fullWidth }
            )
        },
        label = "FromStartToStartAnimContent"
    )
}

@Composable
public fun <S> FromStartToEndAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(DEFAULT_DURATION),
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = animationSpec,
                initialOffsetX = { fullWidth -> -fullWidth }
            ) togetherWith slideOutHorizontally(
                animationSpec = animationSpec,
                targetOffsetX = { fullWidth -> fullWidth }
            )
        },
        label = "FromStartToEndAnimContent"
    )
}

@Composable
public fun <S> FromEndToStartAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(DEFAULT_DURATION),
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = animationSpec,
                initialOffsetX = { fullWidth -> fullWidth }
            ) togetherWith slideOutHorizontally(
                animationSpec = animationSpec,
                targetOffsetX = { fullWidth -> -fullWidth }
            )
        },
        label = "FromEndToStartAnimContent"
    )
}

@Composable
public fun <S> FromEndToEndAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(DEFAULT_DURATION),
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = animationSpec,
                initialOffsetX = { fullWidth -> fullWidth }
            ) togetherWith slideOutHorizontally(
                animationSpec = animationSpec,
                targetOffsetX = { fullWidth -> fullWidth }
            )
        },
        label = "FromEndToEndAnimContent"
    )
}

@Composable
public fun <S> FromBottomEndToBottomEndAnimContent(
    targetState: S,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(DEFAULT_DURATION),
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content,
        transitionSpec = {
            slideIn(
                animationSpec = animationSpec,
                initialOffset = { size -> IntOffset(size.width, size.height) }
            ) togetherWith slideOut(
                animationSpec = animationSpec,
                targetOffset = { size -> IntOffset(size.width, size.height) }
            )
        },
        label = "FromBottomEndToBottomEndAnimContent"
    )
}

@Composable
public fun BottomAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
    )
}

@Composable
public fun TopEndAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandIn(expandFrom = Alignment.TopEnd),
        exit = shrinkOut(shrinkTowards = Alignment.TopEnd),
    )
}

@Composable
public fun TopAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    )
}

@Composable
public fun EndAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandHorizontally(expandFrom = Alignment.End),
        exit = shrinkHorizontally(shrinkTowards = Alignment.End),
    )
}

@Composable
public fun StartAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandHorizontally(expandFrom = Alignment.Start),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start),
    )
}

@Composable
public fun ColumnScope.BottomAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
    )
}

@Composable
public fun ColumnScope.TopAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    )
}

@Composable
public fun RowScope.TopAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        content = content,
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    )
}
