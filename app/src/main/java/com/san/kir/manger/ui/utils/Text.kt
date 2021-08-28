package com.san.kir.manger.ui.utils

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabelText(idRes: Int) {
    Text(
        text = stringResource(id = idRes),
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 0.dp, top = 5.dp)
    )
}

@Composable
fun DialogText(text: String, color: Color = Color.Unspecified, onClick: (() -> Unit) = {}) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DropDownTextField(
    inititalValue: String,
    valuesList: List<String>,
    onChangeValue: (String) -> Unit
) {
    var field by remember { mutableStateOf(inititalValue) }
    onChangeValue(field)

    var dropDownList by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = field,
        onValueChange = { field = it },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { dropDownList = it.isFocused }
    )
    AnimatedVisibility(
        dropDownList,
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Column {
            // Выпадающее меню для выбора категории
            valuesList.forEach { value ->
                MenuText(value) {
                    field = value
                    dropDownList = false
                }
            }
        }
    }
}


@Composable
fun CheckBoxText(
    state: Boolean,
    onChange: (Boolean) -> Unit,
    @StringRes firstTextId: Int,
    @StringRes secondTextId: Int = -1,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onChange(state.not()) }) {

        Checkbox(
            checked = state,
            onCheckedChange = onChange,
            modifier = Modifier.padding(end = 10.dp)
        )

        if (secondTextId == -1)
            Text(text = stringResource(id = firstTextId))
        else
            Text(
                text = stringResource(
                    id = if (state)
                        firstTextId
                    else
                        secondTextId
                )
            )
    }
}

