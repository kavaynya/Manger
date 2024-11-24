package com.san.kir.schedule.ui.task


import android.app.TimePickerDialog
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.san.kir.core.compose.DataTextHelper
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.HorizontalTextRadioGroup
import com.san.kir.core.compose.LabelText
import com.san.kir.core.compose.MultiChoiceList
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.SingleChoiceList
import com.san.kir.core.compose.animation.FromBottomToTopAnimContent
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.FromStartToStartAnimContent
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.utils.PlannedPeriod
import com.san.kir.data.models.utils.PlannedType
import com.san.kir.data.models.utils.PlannedWeek
import com.san.kir.schedule.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskScreen(navigateUp: () -> Unit, itemId: Long) {
    val holder: TaskStateHolder = stateHolder { TaskViewModel(itemId) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()
    val changeAction = rememberLambda { type: ChangeType -> sendAction(TaskAction.Change(type)) }

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = if (state.item.isNew) {
                stringResource(R.string.create_task)
            } else {
                stringResource(R.string.edit_task)
            },
            hasAction = state.backgroundWork.hasBackgrounds,
            actions = {
                FromEndToEndAnimContent(targetState = state.availableAction) {
                    when (it) {
                        AvailableAction.None -> {}
                        AvailableAction.Start -> MenuIcon(Icons.Default.PlayArrow) { sendAction(TaskAction.Start) }
                        AvailableAction.Save -> Row {
                            MenuIcon(Icons.Default.Restore) { sendAction(TaskAction.Restore) }
                            MenuIcon(Icons.Default.Save) { sendAction(TaskAction.Save) }
                        }
                    }
                }
            }
        )
    ) {
        Column(
            modifier = Modifier
                .horizontalInsetsPadding(horizontal = Dimensions.default)
                .fillMaxWidth(),
        ) {
            TypeChanger(type = state.item.type, sendAction = changeAction)

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.half))

            TypeConfig(state, changeAction)

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.half))

            PeriodChanger(period = state.item.period, sendAction = changeAction)

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.half))

            TopAnimatedVisibility(visible = state.item.period == PlannedPeriod.WEEK) {
                PeriodConfig(dayOfWeek = state.item.dayOfWeek, sendAction = changeAction)
            }

            TimeConfig(
                hour = state.item.hour,
                minute = state.item.minute,
                sendAction = changeAction
            )
        }
    }
}

@Composable
private fun ColumnScope.TypeChanger(type: PlannedType, sendAction: (ChangeType) -> Unit) {
    HalfSpacer()
    LabelText(idRes = R.string.update_type)
    DefaultSpacer()
    HorizontalTextRadioGroup(
        dataHelpers = TaskState.types.map { DataTextHelper(it.text, it) },
        initialValue = type,
        onChange = { sendAction(ChangeType.Type(it)) }
    )
    HalfSpacer()
}

@Composable
private fun TypeConfig(state: TaskState, sendAction: (ChangeType) -> Unit) {
    FromBottomToTopAnimContent(targetState = state.item.type) { currentState ->
        when (currentState) {
            PlannedType.MANGA ->
                TypedItem(
                    nonSelectedLabel = R.string.change_manga,
                    value = state.mangaName,
                    nothingValue = R.string.nothing_selected,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = state.item.mangaId,
                        stateList = state.mangaIds,
                        textList = state.mangaNames,
                        onDismiss = dismiss,
                        onSelect = { sendAction(ChangeType.Manga(it)) },
                        onClear = { sendAction(ChangeType.Manga(-1L)) }
                    )
                }

            PlannedType.GROUP ->
                TypedItemList(
                    label = R.string.group_name,
                    initialValue = state.item.groupName,
                    onValueChange = { sendAction(ChangeType.Group(it)) },
                    label2 = R.string.set_up_group,
                    items = state.groupNames
                ) { dismiss ->
                    MultiChoiceList(
                        items = state.item.mangas,
                        textList = state.mangaNames,
                        stateList = state.mangaIds,
                        onDismiss = dismiss,
                        onSelect = { sendAction(ChangeType.Mangas(it)) },
                        onClear = { sendAction(ChangeType.Mangas(emptyList())) }
                    )
                }

            PlannedType.CATEGORY ->
                TypedItem(
                    nonSelectedLabel = R.string.change_category,
                    value = state.categoryName,
                    nothingValue = R.string.category_not_selected,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = state.item.categoryId,
                        stateList = state.categoryIds,
                        textList = state.categoryNames,
                        onDismiss = dismiss,
                        onSelect = { sendAction(ChangeType.Category(it)) },
                        onClear = { sendAction(ChangeType.Category(-1L)) }
                    )
                }

            PlannedType.CATALOG ->
                TypedItem(
                    nonSelectedLabel = R.string.change_catalog,
                    value = state.item.catalog,
                    nothingValue = R.string.catalog_not_selected,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = state.item.catalog,
                        stateList = state.catalogNames,
                        textList = state.catalogNames,
                        onDismiss = dismiss,
                        onSelect = { sendAction(ChangeType.Catalog(it)) },
                        onClear = { sendAction(ChangeType.Catalog("")) }
                    )
                }

            else -> {}
        }
    }
}

@Composable
private fun PeriodChanger(period: PlannedPeriod, sendAction: (ChangeType) -> Unit) {
    HalfSpacer()
    LabelText(R.string.repeat_period)
    DefaultSpacer()
    HorizontalTextRadioGroup(
        dataHelpers = TaskState.periods.map { DataTextHelper(it.text, it) },
        initialValue = period,
        onChange = { sendAction(ChangeType.Period(it)) }
    )
    HalfSpacer()
}

@Composable
private fun PeriodConfig(dayOfWeek: PlannedWeek, sendAction: (ChangeType) -> Unit) {
    Column {
        LabelText(R.string.change_day)

        HorizontalTextRadioGroup(
            dataHelpers = TaskState.weeks.map { DataTextHelper(it.text, it) },
            initialValue = dayOfWeek,
            onChange = { sendAction(ChangeType.Day(it)) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.half))
    }
}

@Composable
private fun TimeConfig(hour: Int, minute: Int, sendAction: (ChangeType) -> Unit) {
    LabelText(R.string.change_time)

    val activity = LocalContext.current as ComponentActivity
    TextButton(onClick = {
        showTimePicker(activity, hour, minute) { hour, minute ->
            sendAction(ChangeType.Time(hour, minute))
        }
    }) {
        Text(
            "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Composable
private fun TypedItem(
    nonSelectedLabel: Int,
    nothingValue: Int,
    value: String,
    dialogContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var dialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        HalfSpacer()

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LabelText(nonSelectedLabel)
            FullWeightSpacer()
            Text(
                text = stringResource(R.string.change).uppercase(),
                modifier = Modifier
                    .clip(RoundedCornerShape(40))
                    .clickable { dialog = true }
                    .padding(Dimensions.quarter),
                color = MaterialTheme.colorScheme.primary
            )
        }

        HalfSpacer()

        Text(
            text = value.ifEmpty { stringResource(nothingValue) },
            color = MaterialTheme.colorScheme.tertiary
        )

        HalfSpacer()

        if (dialog) {
            dialogContent { dialog = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TypedItemList(
    label: Int,
    initialValue: String,
    onValueChange: (String) -> Unit,
    label2: Int,
    items: List<String>,
    dialogContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var dialog by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(initialValue) }
    val interactionSource = remember { MutableInteractionSource() }

    Column {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = Dimensions.default)) {
            LabelText(label)

            BasicTextField(
                value = value,
                onValueChange = {
                    value = it
                    onValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimensions.half),
                textStyle = LocalTextStyle.current.merge(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        placeholder = { Text(stringResource(R.string.placeholder)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                            focusedPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(alpha = 0.8f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(alpha = 0.4f),
                        ),
                        contentPadding = PaddingValues(
                            horizontal = Dimensions.zero,
                            vertical = Dimensions.half
                        )
                    )
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LabelText(label2)
            FullWeightSpacer()
            Text(
                text = stringResource(R.string.change).uppercase(),
                modifier = Modifier
                    .clip(RoundedCornerShape(40))
                    .clickable { dialog = true }
                    .padding(Dimensions.quarter),
                color = MaterialTheme.colorScheme.primary
            )
        }

        FromStartToStartAnimContent(
            targetState = items.isEmpty(),
            modifier = Modifier
                .padding(Dimensions.half)
        ) {
            if (it) {
                Text(stringResource(R.string.nothing_selected))
            } else
                FlowRow {
                    items.forEach { item ->
                        Text(
                            text = item,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = Dimensions.quarter, horizontal = Dimensions.half)
                        )
                    }
                }
        }

        if (dialog) {
            dialogContent { dialog = false }
        }
    }
}

private fun showTimePicker(
    activity: ComponentActivity,
    hour: Int,
    minute: Int,
    updateTime: (hour: Int, minute: Int) -> Unit,
) {
    val dialog = TimePickerDialog(
        /* context = */ activity,
        /* listener = */ { _, h, m -> updateTime(h, m) },
        /* hourOfDay = */ hour,
        /* minute = */ minute,
        /* is24HourView = */ true
    )
    dialog.show()
}
