package com.san.kir.core.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun <T> SingleChoiceList(
    title: Int = -1,
    initialValue: T,
    stateList: List<T>,
    textList: List<String>,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
    onClear: () -> Unit = {}
) {
    BaseListAlert(title, onDismiss, onDismiss, onClear) {
        LazyRadioGroup(
            state = initialValue,
            onSelected = {
                onSelect(it)
                onDismiss()
            },
            stateList = stateList,
            textList = textList,
        )
    }
}

@Composable
fun <T> MultiChoiceList(
    title: Int = -1,
    items: List<T>,
    stateList: List<T>,
    textList: List<String>,
    onDismiss: () -> Unit,
    onSelect: (List<T>) -> Unit,
    onClear: () -> Unit,
) {
    val tempItems = remember(items) { items.toMutableStateList() }
    BaseListAlert(
        title = title,
        onDismiss = onDismiss,
        onSuccess = { onSelect(tempItems) },
        onClear = onClear
    ) {
        LazyColumn {
            items(stateList.size, key = { it }) { index ->
                val state = stateList[index]
                val text = textList[index]

                CheckBoxText(
                    state = tempItems.any { it == state },
                    onChange = {
                        if (it) tempItems.add(state)
                        else tempItems.remove(state)
                    },
                    firstText = text,
//                    modifier = Modifier.padding(vertical = Dimensions.half)
                )
            }
        }
    }
}

@Composable
private fun BaseListAlert(
    title: Int = -1,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onClear: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.clear),
                    modifier = Modifier.clickable {
                        onClear()
                        onDismiss()
                    }
                )

                DefaultSpacer()

                Text(
                    stringResource(R.string.ready),
                    modifier = Modifier.clickable {
                        onSuccess()
                        onDismiss()
                    }
                )

            }
        },
        title = if (title != -1) {
            { Text(stringResource(title)) }
        } else {
            null
        },
        text = content
    )
}
