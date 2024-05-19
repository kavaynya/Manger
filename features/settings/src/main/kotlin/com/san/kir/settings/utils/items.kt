package com.san.kir.settings.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.san.kir.core.compose.CheckBoxText
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.RadioGroup
import com.san.kir.core.compose.SmallestSpacer
import com.san.kir.core.compose.endInsetsPadding
import com.san.kir.core.compose.startInsetsPadding
import com.san.kir.settings.R


@Composable
private fun TemplatePreferenceItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    title: Int,
    subtitle: Int,
    action: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Dimensions.half),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = modifier
                    .startInsetsPadding()
                    .size(Dimensions.Image.bigger),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = "")
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
                SmallestSpacer()
                Text(stringResource(subtitle), style = MaterialTheme.typography.bodySmall)
            }
        }

        Box(
            modifier = Modifier
                .endInsetsPadding()
                .padding(end = Dimensions.half)
                .size(Dimensions.Image.bigger),
            contentAlignment = Alignment.Center,
        ) {
            if (action != null)
                action()
        }
    }
}

@Composable
internal fun PreferenceTitle(id: Int) {
    Column {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimensions.Image.bigger,
                    bottom = Dimensions.half,
                    top = Dimensions.default
                )
        ) {
            Text(
                text = stringResource(id),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
            )
        }
    }
}

@Composable
internal fun <T> ListPreferenceItem(
    title: Int,
    subtitle: Int,
    icon: ImageVector? = null,
    entries: Int,
    entryValues: List<T>,
    initialValue: T,
    onValueChange: (T) -> Unit,
) {
    var dialog by remember { mutableStateOf(false) }
    TemplatePreferenceItem(title = title, subtitle = subtitle, icon = icon) {
        dialog = true
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            confirmButton = {
                TextButton(
                    onClick = { dialog = false },
                    modifier = Modifier.padding(
                        end = Dimensions.default,
                        bottom = Dimensions.default
                    )
                ) {
                    Text(text = "CANCEL")
                }
            },
            title = {
                Text(stringResource(title))
            },
            text = {
                RadioGroup(
                    state = initialValue,
                    onSelected = {
                        onValueChange(it)
                        dialog = false
                    },
                    stateList = entryValues,
                    textList = stringArrayResource(entries).toList(),
                )
            },
        )
    }
}

@Composable
internal fun TogglePreferenceItem(
    title: Int,
    subtitle: Int,
    icon: ImageVector? = null,
    initialValue: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {

    TemplatePreferenceItem(
        title = title, subtitle = subtitle,
        icon = icon,
        action = {
            Switch(
                checked = initialValue,
                onCheckedChange = { onCheckedChange(it) })
        },
        onClick = {
            onCheckedChange(initialValue.not())
        }
    )
}

@Composable
internal fun MultiSelectListPreferenceItem(
    title: Int,
    subtitle: Int,
    icon: ImageVector? = null,
    entries: Int,
    initialValue: List<Boolean>,
    onValueChange: (List<Boolean>) -> Unit
) {
    val items = remember(initialValue) { mutableStateListOf(*initialValue.toTypedArray()) }
    var dialog by remember { mutableStateOf(false) }

    TemplatePreferenceItem(title = title, subtitle = subtitle, icon = icon) {
        dialog = true
    }

    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            confirmButton = {
                TextButton(
                    modifier = Modifier.padding(
                        bottom = Dimensions.default,
                        end = Dimensions.default
                    ),
                    onClick = {
                        onValueChange(items)
                        dialog = false
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            title = {
                Text(stringResource(title))
            },
            text = {
                val textList = stringArrayResource(entries).toList()

                Column {
                    textList.forEachIndexed { index, text ->
                        CheckBoxText(
                            state = items[index],
                            onChange = { items[index] = it },
                            firstText = text,
                            modifier = Modifier.padding(vertical = Dimensions.half)
                        )
                    }
                }
            },
        )
    }
}
