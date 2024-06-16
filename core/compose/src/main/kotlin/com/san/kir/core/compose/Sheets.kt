package com.san.kir.core.compose

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.utils.navigation.DialogBase
import com.san.kir.core.utils.navigation.DialogState

@Composable
public fun <T : Parcelable> TopSheets(
    dialogState: DialogState<T>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: CornerBasedShape = Sheets.shape,
    animationSpec: FiniteAnimationSpec<IntOffset> = Sheets.animationSpec,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    Sheet(
        dialogState,
        Alignment.TopCenter,
        slideInVertically(animationSpec) { -it },
        slideOutVertically(animationSpec) { -it },
        modifier,
        scrimColor,
        containerColor,
        elevation,
        shape.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetContent
    )
}

@Composable
public fun <T : Parcelable> TopEndSheets(
    dialogState: DialogState<T>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: CornerBasedShape = Sheets.shape,
    animationSpec: FiniteAnimationSpec<IntOffset> = Sheets.animationSpec,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    Sheet(
        dialogState,
        Alignment.TopEnd,
        slideInHorizontally(animationSpec) { it },
        slideOutHorizontally(animationSpec) { it },
        modifier,
        scrimColor,
        containerColor,
        elevation,
        shape.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetContent
    )
}

@Composable
public fun <T : Parcelable> BottomSheets(
    dialogState: DialogState<T>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: CornerBasedShape = Sheets.shape,
    animationSpec: FiniteAnimationSpec<IntOffset> = Sheets.animationSpec,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    Sheet(
        dialogState,
        Alignment.BottomCenter,
        slideInVertically(animationSpec) { it },
        slideOutVertically(animationSpec) { it },
        modifier,
        scrimColor,
        containerColor,
        elevation,
        shape.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetContent
    )
}

@Composable
public fun <T : Parcelable> BottomEndSheets(
    dialogState: DialogState<T>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: CornerBasedShape = Sheets.shape,
    animationSpec: FiniteAnimationSpec<IntOffset> = Sheets.animationSpec,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    Sheet(
        dialogState,
        Alignment.BottomEnd,
        slideInHorizontally(animationSpec) { it },
        slideOutHorizontally(animationSpec) { it },
        modifier,
        scrimColor,
        containerColor,
        elevation,
        shape.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetContent
    )
}

@Composable
public fun <T : Parcelable> CenterDialog(
    dialogState: DialogState<T>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: CornerBasedShape = Sheets.shape,
    animationSpec: FiniteAnimationSpec<IntOffset> = Sheets.animationSpec,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    Sheet(
        dialogState,
        Alignment.Center,
        slideInHorizontally(animationSpec) { -it },
        slideOutHorizontally(animationSpec) { it },
        modifier,
        scrimColor,
        containerColor,
        elevation,
        shape.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetContent
    )
}

@Composable
public fun <T : Parcelable> Sheet(
    dialogState: DialogState<T>,
    alignment: Alignment,
    enter: EnterTransition,
    exit: ExitTransition,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: Shape = Sheets.shape,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    DialogBase(dialogState) { state ->
        SheetContent(
            state,
            alignment,
            { dialogState.dismiss() },
            enter,
            exit,
            modifier,
            scrimColor,
            containerColor,
            elevation,
            shape,
            sheetContent
        )
    }
}

@Composable
public fun <T : Parcelable> SheetContent(
    state: T?,
    alignment: Alignment,
    onDismiss: () -> Unit,
    enter: EnterTransition,
    exit: ExitTransition,
    modifier: Modifier = Modifier,
    scrimColor: Color = Sheets.scrimColor,
    containerColor: Color = Sheets.containerColor,
    elevation: Dp = Sheets.Elevation,
    shape: Shape = Sheets.shape,
    sheetContent: @Composable AnimatedVisibilityScope.(T) -> Unit,
) {
    val alpha = rememberFloatAnimatable()

    LaunchedEffect(state) {
        alpha.animateTo(if (state != null) 1f else 0f)
    }

    val dismissModifier =
        if (state == null) {
            Modifier
        } else {
            Modifier.pointerInput(Unit) { detectTapGestures { onDismiss() } }
        }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(scrimColor, alpha = alpha.value)
        }
        AnimatedVisibility(
            visible = state != null,
            modifier = modifier.align(alignment),
            enter = enter,
            exit = exit,
            label = "",
        ) {
            Surface(
                modifier = Modifier.wrapContentSize(alignment),
                shape = shape,
                color = containerColor,
                tonalElevation = elevation,
            ) {
                if (state != null) {
                    sheetContent.invoke(this, state)
                }
            }
        }
    }
}

public object Sheets {
    public val Elevation: Dp = 1.dp
    public val animationSpec: SpringSpec<IntOffset> =
        spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)

    public val scrimColor: Color
        @Composable get() = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)

    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    public val shape: CornerBasedShape
        @Composable get() = MaterialTheme.shapes.extraLarge
}
