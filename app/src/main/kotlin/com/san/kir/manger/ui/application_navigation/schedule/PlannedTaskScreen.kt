package com.san.kir.manger.ui.application_navigation.schedule

import android.app.TimePickerDialog
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.LabelText
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.RadioGroup
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.topBar
import com.san.kir.core.support.PlannedPeriod
import com.san.kir.core.support.PlannedType
import com.san.kir.core.support.PlannedWeek
import com.san.kir.data.models.base.mangaList
import com.san.kir.manger.R
import com.san.kir.manger.utils.compose.MultiChoiceList
import com.san.kir.manger.utils.compose.SingleChoiceList

@Composable
fun PlannedTaskScreen(navigateUp: () -> Unit, viewModel: PlannedTaskViewModel) {
    val categoryNames by viewModel.categoryNameList.collectAsState()
    val categoryIds by viewModel.categoryIdList.collectAsState()
    val categoryName by viewModel.categoryName.collectAsState()

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = viewModel.title,
            actions = {
                MenuIcon(Icons.Default.Save) {
                    viewModel.save(navigateUp)
                }
            }
        )
    ) {
        LabelText(idRes = R.string.planned_task_type_of_update)

        RadioGroup(
            state = viewModel.task.type,
            onSelected = { viewModel.task = viewModel.task.copy(type = it) },
            stateList = PlannedType.values().toList(),
            textList = PlannedType.values().map { stringResource(it.text) }
        )

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        when (viewModel.task.type) {
            PlannedType.MANGA ->
                TypedItem(
                    label = R.string.planned_task_change_manga,
                    value = viewModel.task.manga,
                    nothingValue = R.string.planned_task_manga_unknown,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = viewModel.task.manga,
                        stateList = viewModel.listMangaName,
                        textList = viewModel.listMangaName,
                        onDismiss = dismiss
                    ) { viewModel.task = viewModel.task.copy(manga = it) }
                }
            PlannedType.GROUP ->
                TypedItemList(
                    label = R.string.planned_task_name_of_group,
                    value = viewModel.task.groupName,
                    onValueChange = { viewModel.task = viewModel.task.copy(groupName = it) },
                    label2 = R.string.planned_task_option_of_group,
                    items = viewModel.task.mangaList
                ) { dismiss ->
                    MultiChoiceList(
                        items = viewModel.task.mangaList,
                        textList = viewModel.listMangaName,
                        onDismiss = dismiss,
                    ) {
                        viewModel.task = viewModel.task.copy(
                            groupContent = it.toString().removeSurrounding("[", "]")
                        )
                    }
                }
            PlannedType.CATEGORY ->
                TypedItem(
                    label = R.string.planned_task_change_category,
                    value = categoryName,
                    nothingValue = R.string.planned_task_category_unknown,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = viewModel.task.categoryId,
                        stateList = categoryIds,
                        textList = categoryNames,
                        onDismiss = dismiss
                    ) { viewModel.task = viewModel.task.copy(categoryId = it) }
                }
            PlannedType.CATALOG ->
                TypedItem(
                    label = R.string.planned_task_change_catalog,
                    value = viewModel.task.catalog,
                    nothingValue = R.string.planned_task_catalog_unknown,
                ) { dismiss ->
                    SingleChoiceList(
                        initialValue = viewModel.task.catalog,
                        stateList = viewModel.catalogList,
                        textList = viewModel.catalogList,
                        onDismiss = dismiss,
                        onSelect = { viewModel.task = viewModel.task.copy(catalog = it) }
                    )
                }
            else -> {
            }
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        LabelText(R.string.planned_task_repeat)

        RadioGroup(
            state = viewModel.task.period,
            onSelected = { viewModel.task = viewModel.task.copy(period = it) },
            stateList = PlannedPeriod.values().toList(),
            textList = PlannedPeriod.values().map { stringResource(it.text) }
        )

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        if (viewModel.task.period == PlannedPeriod.WEEK) {
            LabelText(R.string.planned_task_change_day)

            RadioGroup(
                state = viewModel.task.dayOfWeek,
                onSelected = { viewModel.task = viewModel.task.copy(dayOfWeek = it) },
                stateList = PlannedWeek.values().toList(),
                textList = PlannedWeek.values().map { stringResource(it.text) }
            )

            Divider(modifier = Modifier.padding(vertical = 10.dp))
        }

        LabelText(R.string.planned_task_change_time)

        val activity = LocalContext.current as ComponentActivity
        TextButton(onClick = {
            showTimePicker(activity, viewModel.task.hour, viewModel.task.minute) { hour, minute ->
                viewModel.task = viewModel.task.copy(hour = hour, minute = minute)
            }
        }) {
            Text(
                "${viewModel.task.hour}:${viewModel.task.minute}",
                style = MaterialTheme.typography.h3
            )
        }
    }
}


@Composable
private fun TypedItem(
    label: Int,
    nothingValue: Int,
    value: String,
    dialogContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    LabelText(label)

    var dialog by remember { mutableStateOf(false) }

    Text(
        value.ifEmpty { stringResource(nothingValue) },
        modifier = Modifier.padding(vertical = 5.dp)
    )

    TextButton(onClick = { dialog = true }) {
        Text(stringResource(R.string.planned_task_change).toUpperCase(Locale.current))
    }
    if (dialog) {
        dialogContent { dialog = false }
    }
}

@Composable
private fun TypedItemList(
    label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    label2: Int,
    items: List<String>,
    dialogContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    LabelText(label)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    )

    LabelText(label2)

    if (items.isEmpty())
        Text(stringResource(R.string.planned_task_group_unknown))
    else
        Column { items.forEach { item -> Text(item) } }

    var dialog by remember { mutableStateOf(false) }

    TextButton(onClick = { dialog = true }) {
        Text(stringResource(R.string.planned_task_change).toUpperCase(Locale.current))
    }
    if (dialog) {
        dialogContent { dialog = false }
    }
}

private fun showTimePicker(
    activity: ComponentActivity,
    hour: Int,
    minute: Int,
    updateTime: (hour: Int, minute: Int) -> Unit,
) {
    val dialog = TimePickerDialog(activity, { _, h, m -> updateTime(h, m) }, hour, minute, true)
    dialog.show()
}
