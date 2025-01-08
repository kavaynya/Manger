package com.san.kir.core.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.san.kir.core.compose.animation.animateToDelayed
import com.san.kir.core.compose.animation.fastAnimateTo
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.utils.append
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
public fun createDismissStates(
    size: Int,
    density: Density,
    scope: CoroutineScope
): SnapshotStateList<SwipeToDismissBoxState> {
    val states = mutableStateListOf<SwipeToDismissBoxState>()

    repeat(size) { mainIndex ->
        states += SwipeToDismissBoxState(
            initialValue = SwipeToDismissBoxValue.Settled,
            density = density,
            confirmValueChange = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        states.onEachIndexed { index, state -> if (index != mainIndex) scope.launch { state.reset() } }
                        true
                    }

                    SwipeToDismissBoxValue.Settled -> true
                    else -> false
                }
            },
            positionalThreshold = { it / 2 }
        )
    }

    return states
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun SwipeToDelete(
    state: SwipeToDismissBoxState,
    resetText: Int,
    agreeText: Int,
    durationDismissConfirmation: Int,
    onSuccessDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    resetDesc: Int? = null,
    endButtonPadding: Dp = SwipeToDeleteDefaults.EndButtonEndPadding,
    dismissEndPadding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val params = rememberSharedParams()
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val access = rememberFloatAnimatable()
    val isSuccessDismiss by remember { derivedStateOf { access.value == 1f } }

    LaunchedEffect(pressed) {
        access.stop()
        if (!pressed) {
            access.fastAnimateTo(0f)
        } else {
            access.animateToDelayed(1f, duration = durationDismissConfirmation)
        }
    }

    LaunchedEffect(isSuccessDismiss) {
        if (isSuccessDismiss) {
            onSuccessDismiss()
            state.reset()
        }
    }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(SwipeToDeleteDefaults.MainItemShape)
                    .background(SwipeToDeleteDefaults.backgroundItemContainerColor)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(access.value)
                        .background(MaterialTheme.colorScheme.primary, SwipeToDeleteDefaults.MainItemShape)
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(
                                stringResource(resetText),
                                MaterialTheme.typography.titleLarge.copy(
                                    color = SwipeToDeleteDefaults.backgroundItemContentColor,
                                    lineHeight = 25.sp
                                ),
                            )
                            if (resetDesc != null) {
                                append(
                                    stringResource(resetDesc),
                                    MaterialTheme.typography.bodySmall.copy(
                                        color = SwipeToDeleteDefaults.backgroundItemContentColor,
                                        lineHeight = 13.sp
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dimensions.default)
                    )

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.padding(horizontal = Dimensions.middle),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SwipeToDeleteDefaults.backgroundItemContentColor,
                        ),
                        interactionSource = interactionSource
                    ) {
                        Text(stringResource(agreeText), style = MaterialTheme.typography.bodyLarge)
                    }

                    OutlinedIconButton(
                        onClick = { scope.launch { state.reset() } },
                        modifier = Modifier.padding(end = endButtonPadding),
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            contentColor = SwipeToDeleteDefaults.backgroundItemContentColor
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "")
                    }
                }
            }
        },
        enableDismissFromEndToStart = enabled,
        enableDismissFromStartToEnd = enabled,
        modifier = Modifier
            .saveParams(params)
            .then(modifier)
    ) {
        Box(
            Modifier
                .offset {
                    val itemWidth = params.bounds.width
                    val offset = state.requireOffset()
                    val endPadding = dismissEndPadding.toPx()

                    if (itemWidth > 0f && itemWidth > endPadding && itemWidth - offset < endPadding) {
                        return@offset IntOffset(-(offset - itemWidth + endPadding).roundToInt(), 0)
                    }
                    IntOffset.Zero
                }
                .background(MaterialTheme.colorScheme.surface, SwipeToDeleteDefaults.SpecItemShape),
            content = content
        )
    }
}


public object SwipeToDeleteDefaults {
    public val EndButtonEndPadding: Dp = Dimensions.default
    public val MainItemShape: RoundedCornerShape = RoundedCornerShape(50)
    public val SpecItemShape: RoundedCornerShape = RoundedCornerShape(49)

    public val backgroundItemContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onError

    public val backgroundItemContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.error
}
