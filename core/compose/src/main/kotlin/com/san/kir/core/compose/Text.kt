package com.san.kir.core.compose

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.san.kir.core.compose.animation.StartAnimatedVisibility

@Composable
public fun LabelText(idRes: Int) {
    Text(
        text = stringResource(idRes),
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic
    )
}

@Composable
public fun DialogText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
public fun DropDownTextField(
    inititalValue: String,
    valuesList: List<String>,
    onChangeValue: (String) -> Unit,
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
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Column {
            // Выпадающее меню для выбора категории
            valuesList.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        field = value
                        dropDownList = false
                    }
                )
            }
        }
    }
}

@Composable
public fun CheckBoxText(
    state: Boolean,
    onChange: (Boolean) -> Unit,
    @StringRes firstTextId: Int,
    @StringRes secondTextId: Int = -1,
) {
    CheckBoxText(
        state = state,
        onChange = onChange,
        firstText = stringResource(firstTextId),
        secondText = if (secondTextId != -1) stringResource(secondTextId) else ""
    )
}

@Composable
public fun CheckBoxText(
    modifier: Modifier = Modifier,
    state: Boolean,
    onChange: (Boolean) -> Unit,
    firstText: String,
    secondText: String = "",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.default))
            .clickable { onChange(state.not()) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = state,
            onCheckedChange = onChange,
            modifier = modifier.padding(end = 10.dp)
        )

        if (secondText.isEmpty())
            Text(text = firstText)
        else
            Text(text = if (state) firstText else secondText)
    }
}

// Настроенное текствое поле с кнопкой очистки
@Composable
public fun SearchTextField(
    initialValue: String,
    onChangeValue: (String) -> Unit,
) {
    var searchText by rememberSaveable {
        onChangeValue(initialValue)
        mutableStateOf(initialValue)
    }

    val canClearText by remember { derivedStateOf { searchText.isNotEmpty() } }

    OutlinedTextField(
        value = searchText,
        onValueChange = {
            if (searchText != it) {
                searchText = it
                onChangeValue(it)
            }
        },
        leadingIcon = { Icon(Icons.Default.Search, "search") },
        trailingIcon = {
            StartAnimatedVisibility(canClearText) {
                IconButton(
                    onClick = {
                        searchText = ""
                        onChangeValue("")
                    },
                ) { Icon(Icons.Default.Close, "") }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .horizontalInsetsPadding(horizontal = Dimensions.half),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
            errorTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}

@Composable
public fun TextWithFirstWordBold(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        AnnotatedString(
            text,
            spanStyles = Fonts.Annotated.bold(text.indexOf(":"))
        ),
        textAlign = textAlign,
        maxLines = maxLines,
        modifier = modifier.padding(vertical = Dimensions.quarter)
    )
}
