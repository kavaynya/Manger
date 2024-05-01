package com.san.kir.catalog.ui.addStandart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.san.kir.catalog.R
import com.san.kir.core.compose.DialogText
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.viewModel.stateHolder

@Composable
fun AddStandartScreen(
    navigateUp: () -> Unit,
    url: String,
) {
    val holder: AddStandartViewModel = stateHolder { AddStandartViewModel() }
    val state by holder.state.collectAsState()

    LaunchedEffect(Unit) { holder.sendAction(AddStandartEvent.Set(url)) }

    ScreenContent(
        topBar = topBar(
            title = stringResource(R.string.add_manga_screen_title),
            navigationButton = NavigationButton.Back(navigateUp)
        ),
    ) {
        Content(state, holder::sendAction, navigateUp)
    }
}

@Composable
private fun ColumnScope.Content(
    state: AddStandartState,
    sendEvent: (AddStandartEvent) -> Unit,
    closeBtnAction: () -> Unit,
) {
    TextWithValidate(state.categoryName) { sendEvent(AddStandartEvent.UpdateText(it)) }

    MessageAboutCreatingNewCategory(state.createNewCategory)

    ListOfAvailableCategories(state.availableCategories) { sendEvent(AddStandartEvent.UpdateText(it)) }

    Process(state)

    Spacer(modifier = Modifier.weight(1f, true))

    FromEndToEndAnimContent(
        targetState = state.processState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.default),
        contentAlignment = Alignment.CenterEnd
    ) {
        when (it) {
            ProcessState.Complete, ProcessState.Error ->
                Button(onClick = { closeBtnAction() }) {
                    Text(text = stringResource(id = R.string.add_manga_close_btn))
                }

            ProcessState.Load, ProcessState.None -> Button(
                onClick = { sendEvent(AddStandartEvent.StartProcess) },
                enabled = state.hasAllow
            ) {
                Text(text = stringResource(id = R.string.add_manga_screen_continue))
            }
        }
    }
}

@Composable
private inline fun TextWithValidate(
    value: String,
    crossinline onValueChange: (String) -> Unit,
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimensions.half, bottom = Dimensions.quarter),
        placeholder = { Text(stringResource(id = R.string.add_manga_screen_item)) },
    )
}

@Composable
private inline fun ColumnScope.MessageAboutCreatingNewCategory(visible: Boolean) {
    TopAnimatedVisibility(visible) {
        Text(
            stringResource(id = R.string.add_manga_screen_add_new),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ListOfAvailableCategories(
    listOfCategories: List<String>,
    onItemSelect: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisAlignment = FlowMainAxisAlignment.End,
        crossAxisAlignment = FlowCrossAxisAlignment.End,
    ) {
        listOfCategories.forEach { item ->
            Card(modifier = Modifier
                .padding(Dimensions.quarter)
                .clickable { onItemSelect(item) }) {
                Text(item, modifier = Modifier.padding(Dimensions.smaller))
            }
        }
    }
}

@Composable
private fun ColumnScope.Process(state: AddStandartState) {
    TopAnimatedVisibility(state.createNewCategory) {
        DialogText(stringResource(R.string.add_manga_screen_created_category, state.categoryName))
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.categoryChanged) {
        DialogText(stringResource(R.string.add_manga_screen_changed_category, state.categoryName))
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.prevAndUpdateManga) {
        DialogText(stringResource(R.string.add_manga_screen_update_manga))
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.prevAndCreatedFolder) {
        DialogText(stringResource(R.string.add_manga_screen_created_folder))
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.prevAndSearchChapters) {
        DialogText(stringResource(R.string.add_manga_screen_search_chapters))
    }

    TopAnimatedVisibility(state.progress >= ProcessStatus.allComplete) {
        DialogText(stringResource(R.string.add_manga_screen_all_complete))
    }

    TopAnimatedVisibility(state.processState is ProcessState.Error) {
        DialogText(stringResource(R.string.add_manga_screen_error))
    }

    TopAnimatedVisibility(state.processState is ProcessState.Load) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.default)
                .padding(vertical = Dimensions.quarter)
        )
    }
}


@Preview(
    name = "PreviewListOfAvailableCategories Light",
    group = "ListOfAvailableCategories",
    showBackground = true,
)
@Composable
fun PreviewListOfAvailableCategoriesLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        ListOfAvailableCategories(listOfCategories = listOf("Test 1", "Test 2", "Test 3"),
                                  onItemSelect = {})
    }
}

@Preview(
    name = "PreviewListOfAvailableCategories Dark",
    group = "ListOfAvailableCategories",
)
@Composable
fun PreviewListOfAvailableCategoriesDark() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ListOfAvailableCategories(listOfCategories = listOf("Test 1", "Test 2", "Test 3"),
                                  onItemSelect = {})
    }
}
