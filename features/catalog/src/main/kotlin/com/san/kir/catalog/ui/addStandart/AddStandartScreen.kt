package com.san.kir.catalog.ui.addStandart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.san.kir.catalog.R
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddStandartScreen(navigateUp: () -> Unit, url: String) {

    val holder: AddStandartViewModel = stateHolder { AddStandartViewModel(url) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    ScreenContent(
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(canScroll = { false }),
        additionalPadding = Dimensions.default,
        topBar = topBar(
            title = stringResource(R.string.manga_adding),
            navigationButton = NavigationButton.Back(navigateUp)
        ),
    ) {
        Column(modifier = Modifier.weight(1f).horizontalInsetsPadding()) {
            TextWithValidate(state) { sendAction(AddStandartAction.UpdateText(it)) }
            MessageAboutCreatingNewCategory(state.createNewCategory)

            TopAnimatedVisibility(visible = state.processState !is ProcessState.Load) {
                ListOfAvailableCategories(state.availableCategories) {
                    sendAction(AddStandartAction.UpdateText(it))
                }
            }

            Process(state)

            FullWeightSpacer()

            FromEndToEndAnimContent(
                targetState = state.processState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.default),
                contentAlignment = Alignment.CenterEnd
            ) {
                when (it) {
                    ProcessState.Complete -> Button(onClick = navigateUp) {
                        Text(stringResource(R.string.close))
                    }

                    ProcessState.None -> Button(
                        onClick = { sendAction(AddStandartAction.StartProcess) },
                        enabled = state.hasAllow
                    ) {
                        Text(stringResource(R.string.proceed))
                    }

                    ProcessState.Error -> Button(
                        onClick = { sendAction(AddStandartAction.StartProcess) },
                        enabled = state.hasAllow
                    ) {
                        Text(stringResource(R.string.try_again))
                    }

                    ProcessState.Load -> Unit
                }
            }
        }
    }
}

@Composable
private fun TextWithValidate(state: AddStandartState, onValueChange: (String) -> Unit) {
    var valueField by rememberSaveable(state.categoryName) { mutableStateOf(state.categoryName) }
    val canClearText by remember(state.categoryName) { derivedStateOf { state.categoryName.isNotEmpty() } }
    OutlinedTextField(
        value = valueField,
        onValueChange = {
            if (valueField != it) {
                valueField = it
                onValueChange(it)
            }
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.quarter),
        enabled = state.processState.canEdit(),
        isError = state.hasAllow.not(),
        placeholder = { Text(stringResource(id = R.string.select_category)) },
        leadingIcon = { Icon(Icons.Default.Category, "category") },
        trailingIcon = {
            StartAnimatedVisibility(canClearText) {
                IconButton(
                    onClick = {
                        valueField = ""
                        onValueChange("")
                    },
                    enabled = state.processState.canEdit(),
                ) { Icon(Icons.Default.Close, "") }
            }
        },
    )
}

@Composable
private fun ColumnScope.MessageAboutCreatingNewCategory(visible: Boolean) {
    TopAnimatedVisibility(visible) {
        Text(
            stringResource(id = R.string.will_be_create_new_category),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListOfAvailableCategories(
    listOfCategories: List<String>,
    onItemSelect: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        listOfCategories.forEach { item ->
            Card(
                modifier = Modifier
                    .padding(Dimensions.half)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { onItemSelect(item) }
            ) {
                Text(
                    item,
                    modifier = Modifier.padding(
                        horizontal = Dimensions.default,
                        vertical = Dimensions.half
                    )
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.Process(state: AddStandartState) {

    TopAnimatedVisibility(state.progress >= ProcessStatus.CATEGORY_CHANGED) {
        Title(R.string.changed_category_format, state.categoryName)
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.PREV_AND_UPDATE_MANGA) {
        Title(R.string.update_info_and_add_to_library)
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.PREV_AND_CREATED_FOLDER) {
        Title(R.string.creating_manga_folder)
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.PREV_AND_SEARCH_CHAPTERS) {
        Title(R.string.run_chapter_finding_task)
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.ALL_COMPLETE) {
        Title(R.string.all_tasks_completed)
    }

    TopAnimatedVisibility(state.processState is ProcessState.Error) {
        Title(R.string.add_manga_screen_error, color = MaterialTheme.colorScheme.error)
    }

    TopAnimatedVisibility(state.processState is ProcessState.Load) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.quarter)
                .height(Dimensions.half),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun Title(text: Int, arg: String? = null, color: Color = Color.Unspecified) {
    Text(
        text = if (arg == null) stringResource(text) else stringResource(text, arg),
        modifier = Modifier.padding(vertical = Dimensions.half),
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}


@Preview(
    name = "PreviewListOfAvailableCategories Light",
    group = "ListOfAvailableCategories",
    showBackground = true,
)
@Composable
private fun PreviewListOfAvailableCategoriesLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        ListOfAvailableCategories(
            listOfCategories = listOf("Test 1", "Test 2", "Test 3"),
            onItemSelect = {})
    }
}

@Preview(
    name = "PreviewListOfAvailableCategories Dark",
    group = "ListOfAvailableCategories",
)
@Composable
private fun PreviewListOfAvailableCategoriesDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ListOfAvailableCategories(
            listOfCategories = listOf("Test 1", "Test 2", "Test 3"),
            onItemSelect = {})
    }
}
