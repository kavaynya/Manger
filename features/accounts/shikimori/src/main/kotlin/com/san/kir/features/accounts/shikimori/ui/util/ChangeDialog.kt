package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.san.kir.core.compose.BottomSheets
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.Fonts
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.utils.navigation.DialogState
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriStatus

@Composable
internal fun ChangeDialog(
    state: DialogState<AccountMangaItem>,
    onChange: (AccountMangaItem) -> Unit
) {
    BottomSheets(
        state,
    ) {
        var innerItem by remember { mutableStateOf(it) }
        val statuses = LocalContext.current.resources.getStringArray(R.array.statuses)
        var expandedMenu by remember { mutableStateOf(false) }

        BoxWithConstraints {
            val textWidth = maxWidth * 0.4f
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(Dimensions.default)
                ) {
                    Text(
                        stringResource(R.string.mutable_properties),
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    IconButton(onClick = state::dismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                WithLabel(R.string.status, textWidth) {
                    Box(modifier = Modifier.fillMaxWidth().padding(end = Dimensions.default)) {
                        Text(
                            statuses[innerItem.status.ordinal],
                            modifier = Modifier
                                .padding(vertical = Dimensions.half)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(Dimensions.default),
                                )
                                .clickable { expandedMenu = !expandedMenu }
                                .clip(RoundedCornerShape(Dimensions.default))
                                .padding(Dimensions.half)
                                .align(Alignment.CenterEnd),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
                StatusChanger(expandedMenu, statuses) { expand, status ->
                    innerItem = innerItem.copy(status = status)
                    expandedMenu = expand
                }

                DefaultSpacer()

                WithLabel(R.string.profile_item_chapters_change, textWidth) {
                    CountChanger(
                        initialCount = innerItem.read,
                        visibleMaxCount = innerItem.all,
                        maxCount = Int.MAX_VALUE,
                        onChange = {
                            onChange(innerItem.copy(read = it, status = ShikimoriStatus.Watching))
                        },
                    )
                }

                DefaultSpacer()

                WithLabel(R.string.profile_item_rewrite_change, textWidth) {
                    CountChanger(
                        initialCount = innerItem.rewatches,
                        onChange = { innerItem = innerItem.copy(rewatches = it) },
                    )
                }

                DefaultSpacer()

                WithLabel(R.string.profile_item_score_change, textWidth) {
                    CountChanger(
                        initialCount = innerItem.userScore,
                        visibleMaxCount = 10,
                        onChange = { innerItem = innerItem.copy(userScore = it) },
                    )
                }

                DefaultSpacer()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomInsetsPadding(right = Dimensions.default),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = {
                        onChange(innerItem)
                        state.dismiss()
                    }) {
                        Text(stringResource(R.string.change))
                    }
                }

                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
            }
        }
    }
}

@Composable
private fun WithLabel(textRes: Int, width: Dp, content: @Composable RowScope.() -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(textRes),
            modifier = Modifier
                .width(width)
                .padding(end = Dimensions.default),
            textAlign = TextAlign.End
        )
        content()
    }
}

@Composable
private fun ColumnScope.StatusChanger(
    expandMenu: Boolean,
    statuses: Array<String>,
    onChange: (Boolean, ShikimoriStatus) -> Unit,
) {
    BottomAnimatedVisibility(
        expandMenu, modifier = Modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(Dimensions.default)
        )
    ) {
        Column {
            ShikimoriStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(statuses[status.ordinal]) },
                    onClick = { onChange(false, status) },
                )
            }
        }
    }
}

@Composable
private fun CountChanger(
    initialCount: Int,
    visibleMaxCount: Int? = null,
    maxCount: Int? = null,
    onChange: (Int) -> Unit,
) {
    val counter = remember { mutableIntStateOf(initialCount) }
    var isWrongText by remember { mutableStateOf(false) }

    fun prepareCounter(
        maxCount: Int?,
        onChange: (Int) -> Unit,
        counter: MutableIntState,
        newValue: Int
    ) {
        var tempValue = maxOf(0, newValue)
        maxCount?.let { max -> tempValue = minOf(tempValue, max) }

        counter.intValue = tempValue
        onChange(tempValue)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            prepareCounter(maxCount, onChange, counter, counter.intValue - 1)
        }) {
            Icon(Icons.Default.Remove, contentDescription = "Decrement")
        }

        OutlinedTextField(
            value = "${counter.intValue}",
            onValueChange = {
                kotlin.runCatching {
                    isWrongText = false
                    prepareCounter(maxCount, onChange, counter, initialCount)
                }.onFailure { isWrongText = true }
            },
            isError = isWrongText,
            modifier = Modifier.weight(1f)
        )

        visibleMaxCount?.let { Text(text = " / $visibleMaxCount", fontSize = Fonts.Size.bigger) }

        IconButton(onClick = {
            prepareCounter(maxCount, onChange, counter, counter.intValue + 1)
        }) {
            Icon(Icons.Default.Add, contentDescription = "Increment")
        }
    }
}

@Preview
@Composable
internal fun CountChangerPreview() {
    MaterialTheme {
        CountChanger(initialCount = 4, onChange = {}, maxCount = 10)
    }
}


