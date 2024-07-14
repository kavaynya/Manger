package com.san.kir.categories.ui.category

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.TextRotateUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import com.san.kir.categories.R
import com.san.kir.core.compose.AlertDialog
import com.san.kir.core.compose.DataTextHelper
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.RotateToggleButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.VerticalRadioGroup
import com.san.kir.core.compose.animation.FromStartToEndAnimContent
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberFloatAnimatable
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.navigation.show
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.utils.SortLibraryUtil
import kotlin.math.roundToInt


private val TrackHeight = 30.dp

public fun <T> defaultAnimationSpec(): SpringSpec<T> {
    return spring(0.7f, 700.0f, null)
}

private fun <T> rotateAnimationSpec(): SpringSpec<T> {
    return spring(0.5f, 600.0f, null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryScreen(
    navigateUp: () -> Unit,
    categoryName: String,
) {
    val stateHolder: CategoryStateHolder = stateHolder { CategoryViewModel(categoryName) }
    val state by stateHolder.state.collectAsStateWithLifecycle()
    val sendAction = stateHolder.rememberSendAction()

    val deleteDialog = rememberDialogState<EmptyDialogData>(onSuccess = {
        sendAction(CategoryAction.Delete)
        navigateUp()
    })


    ScreenContent(
        additionalPadding = Dimensions.default,
        topBar = topBar(
            title = stringResource(
                if (state.isCreatedNew) R.string.create_category
                else R.string.edit_category
            ),
            navigationButton = NavigationButton.Back(navigateUp),
            actions = {
                StartAnimatedVisibility(state.hasChanges && state.error.has.not()) {
                    MenuIcon(
                        icon = if (state.isCreatedNew) Icons.Default.CreateNewFolder else Icons.Default.Save,
                    ) { sendAction(CategoryAction.Save) }
                }

                StartAnimatedVisibility(state.hasAll.not() && state.isCreatedNew.not()) {
                    MenuIcon(icon = Icons.Default.Delete, onClick = deleteDialog::show)
                }
            }
        ),
    ) {
        TextWithValidate(
            categoryName = state.category.name,
            hasAll = state.hasAll,
            error = state.error,
            sendAction = sendAction,
        )

        DefaultSpacer()

        ChangeSortType(state.category.typeSort, state.category.isReverseSort, sendAction)

        DefaultSpacer()

        ChangeTabVisibility(state.category.isVisible, sendAction)

        DefaultSpacer()

        ChangePortraitOptions(state.category.spanPortrait, sendAction)

        DefaultSpacer()

        ChangeLandscapeOptions(state.category.spanLandscape, sendAction)
    }

    AlertDialog(state = deleteDialog, text = R.string.are_you_sure_want_delete)
}

@Composable
private fun TextWithValidate(
    categoryName: String,
    hasAll: Boolean,
    error: ErrorState,
    sendAction: (CategoryAction) -> Unit,
) {
    var valueField by remember { mutableStateOf(categoryName) }

    OutlinedTextField(
        value = valueField,
        onValueChange = {
            valueField = it
            sendAction(CategoryAction.Update(newName = it))
        },
        enabled = hasAll.not(),
        placeholder = { Text(stringResource(R.string.enter_name)) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalInsetsPadding(),
        isError = error.has
    )

    TopAnimatedVisibility(visible = error.has && hasAll.not()) {
        Text(
            text = error.validate,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Red,
        )
    }
}

@Composable
private fun ChangeSortType(
    typeSort: String,
    isReverseSort: Boolean,
    sendAction: (CategoryAction) -> Unit
) {

    Row(
        modifier = Modifier
            .horizontalInsetsPadding()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(Dimensions.default)
            )
            .padding(Dimensions.half),
    ) {
        VerticalRadioGroup(
            dataHelpers = listOf(
                DataTextHelper(R.string.by_date_added, SortLibraryUtil.ADD),
                DataTextHelper(R.string.in_alphabet_order, SortLibraryUtil.ABC),
                DataTextHelper(R.string.by_frequency_of_use, SortLibraryUtil.POP),
            ),
            initialValue = typeSort
        ) { sendAction(CategoryAction.Update(newTypeSort = it)) }

        RotateToggleButton(icon = Icons.Outlined.TextRotateUp, state = isReverseSort) {
            sendAction(CategoryAction.Update(newReverseSort = isReverseSort.not()))
        }
    }
}

@Composable
private fun ChangeTabVisibility(isVisible: Boolean, sendAction: (CategoryAction) -> Unit) {
    val colorContainer by animateColorAsState(
        if (isVisible) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )

    val colorContent by animateColorAsState(
        if (isVisible) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )

    Surface(
        checked = isVisible,
        onCheckedChange = { sendAction(CategoryAction.Update(newVisible = isVisible.not())) },
        modifier = Modifier
            .semantics { role = Role.Checkbox }
            .horizontalInsetsPadding(),
        shape = RoundedCornerShape(50),
        color = colorContainer,
        contentColor = colorContent
    ) {
        Box(modifier = Modifier.padding(Dimensions.default)) {
            FromStartToEndAnimContent(isVisible, animationSpec = defaultAnimationSpec()) {
                when (it) {
                    true -> Text(stringResource(R.string.category_is_visible))
                    false -> Text(stringResource(R.string.category_is_hide))
                }
            }
        }
    }
}

@Composable
private fun ChangePortraitOptions(span: Int, sendAction: (CategoryAction) -> Unit) {
    SliderWithText(
        textId = R.string.portrait_orientation,
        state = span,
        maxPosition = 5,
        onChange = { sendAction(CategoryAction.Update(newSpanPortrait = it)) }
    )
}

@Composable
private fun ChangeLandscapeOptions(span: Int, sendAction: (CategoryAction) -> Unit) {
    SliderWithText(
        textId = R.string.landscape_orientation,
        state = span,
        maxPosition = 7,
        onChange = { sendAction(CategoryAction.Update(newSpanLandscape = it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderWithText(
    @StringRes textId: Int,
    state: Int,
    maxPosition: Int,
    onChange: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant
    val activeTrackColor = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()
    val sliderLabel = MaterialTheme.typography.titleSmall
    val activeSliderLabel = MaterialTheme.typography.titleLarge
    val interactionSource = remember { MutableInteractionSource() }

    val innerState = rememberFloatAnimatable(state.toFloat())
    val dragged by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(state) {
        innerState.animateTo(state.toFloat(), defaultAnimationSpec())
    }

    Column(
        modifier = Modifier
            .horizontalInsetsPadding()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(Dimensions.default)
            )
            .padding(Dimensions.default)
    ) {
        Text(stringResource(textId))

        Slider(
            value = innerState.value,
            onValueChange = {
                scope.defaultLaunch {
                    if (dragged) innerState.snapTo(it) else onChange(it.roundToInt())
                }
            },
            valueRange = 1f..maxPosition.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.default),
            onValueChangeFinished = { if (dragged) onChange(innerState.value.roundToInt()) },
            interactionSource = interactionSource,
            thumb = { Thumb(interactionSource, activeTrackColor) },
            track = { sliderState ->
                Track(
                    sliderState,
                    maxPosition,
                    inactiveTrackColor,
                    activeTrackColor,
                    textMeasurer,
                    sliderLabel,
                    activeSliderLabel
                )
            }
        )
    }
}

@Composable
private fun Thumb(interactionSource: MutableInteractionSource, color: Color) {
    Canvas(
        modifier = Modifier
            .size(TrackHeight)
            .indication(interactionSource, null)
            .hoverable(interactionSource)
    ) {
        drawCircle(
            color = color,
            radius = size.width / 2,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Track(
    sliderState: SliderState,
    maxPosition: Int,
    inactiveTrackColor: Color,
    activeTrackColor: Color,
    textMeasurer: TextMeasurer,
    sliderLabel: TextStyle,
    activeSliderLabel: TextStyle
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(TrackHeight - 2.dp)
    ) {
        val sliderIndex = (sliderState.valueRange.endInclusive * (maxPosition - 1)).toInt()

        var measureText: TextLayoutResult
        repeat(maxPosition) { index ->
            measureText = textMeasurer.measure(
                text = "${index + 1}",
                style = if (index == sliderIndex) activeSliderLabel else sliderLabel,
            )
            drawText(
                textLayoutResult = measureText,
                color = if (index == sliderIndex) activeTrackColor else inactiveTrackColor,
                topLeft = Offset(
                    (size.width / (maxPosition - 1) * index) - measureText.size.center.x,
                    center.y - measureText.size.center.y
                ),
            )
        }
    }
}
