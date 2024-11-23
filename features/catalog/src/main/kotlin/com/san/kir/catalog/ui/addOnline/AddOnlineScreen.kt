package com.san.kir.catalog.ui.addOnline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertLink
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.san.kir.catalog.R
import com.san.kir.core.compose.CloseIcon
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.Fonts
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddOnlineScreen(
    navigateUp: () -> Unit,
    navigateToNext: (String, SharedParams) -> Unit,
) {
    val holder: AddOnlineStateHolder = stateHolder { AddOnlineViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    holder.OnEvent { event ->
        when (event) {
            is AddOnlineEvent.ToUp -> navigateUp()
            is AddOnlineEvent.ToNext -> navigateToNext(event.url, event.params)
        }
    }

    ScreenContent(
        additionalPadding = Dimensions.default,
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.adding_new_manga),
            hasAction = state.isCheckingUrl
        ),
    ) {
        Content(state = state, sendAction = sendAction)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.Content(
    state: AddOnlineState,
    sendAction: (Action) -> Unit,
) {
    var enteredText by remember { mutableStateOf("") }

    // Текстовое поле ввода ссылки
    OutlinedTextField(
        value = enteredText,
        onValueChange = {
            enteredText = it
            sendAction(AddOnlineAction.Update(it))
        },
        singleLine = true,
        isError = state.isErrorAvailable,
        placeholder = { Text(stringResource(R.string.enter_manga_link)) },
        modifier = Modifier
            .fillMaxWidth()
            .horizontalInsetsPadding(),
        leadingIcon = { Icon(Icons.Default.InsertLink, "insert link") },
        trailingIcon = {
            CloseIcon(enteredText.isNotEmpty()) {
                enteredText = ""
                sendAction(AddOnlineAction.Update(""))
            }
        },
    )

    ClipboardText {
        enteredText = it
        sendAction(AddOnlineAction.Update(it))
    }

    HalfSpacer()

    // Сообщение об ошибке
    AnimatedVisibility(visible = state.isErrorAvailable) {
        Text(
            stringResource(R.string.library_add_manga_error),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }

    // Вывод катологов, которым может соответсвтовать введенный адрес ссылки
    CatalogChips(state.validatesCatalogs) {
        enteredText = it
        sendAction(AddOnlineAction.Update(it))
    }

    FullWeightSpacer()

    // Кнопки
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalInsetsPadding(top = Dimensions.default),
        horizontalArrangement = Arrangement.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Button(
            onClick = { sendAction(ReturnEvents(AddOnlineEvent.ToUp)) },
            modifier = Modifier.padding(end = Dimensions.default)
        ) {
            Text(stringResource(R.string.cancel))
        }

        val params = rememberSharedParams()
        Button(
            onClick = { sendAction(ReturnEvents(AddOnlineEvent.ToNext(enteredText, params))) },
            enabled = state.isEnableAdding,
            modifier = Modifier.saveParams(params)
        ) {
            Text(stringResource(R.string.add))
        }
    }
}

@Composable
private fun ClipboardText(onPaste: (String) -> Unit) {
    val clipboardText = LocalClipboardManager.current.getText()?.text ?: ""

    TopAnimatedVisibility(clipboardText.isNotBlank()) {
        Card(
            shape = RoundedCornerShape(bottomStart = Dimensions.default, bottomEnd = Dimensions.default),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaste(clipboardText) }
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimensions.half)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.insert_from_clipboard),
                    fontSize = Fonts.Size.bigger
                )

                Text(
                    clipboardText,
                    fontSize = Fonts.Size.less,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CatalogChips(validatesCatalogs: List<String>, onClick: (String) -> Unit) {
    BottomAnimatedVisibility(visible = validatesCatalogs.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalInsetsPadding()
        ) {
            validatesCatalogs.forEach { item ->
                Card(
                    modifier = Modifier
                        .padding(Dimensions.quarter)
                        .clickable { onClick(item) }
                ) {
                    Text(
                        item,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = Dimensions.quarter, horizontal = Dimensions.half)
                    )
                }
            }
        }
    }
}
