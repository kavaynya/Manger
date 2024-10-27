package com.san.kir.core.compose

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.san.kir.core.utils.navigation.DialogState


private val DialogPadding = PaddingValues(24.dp)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)
private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 12.dp

private fun text(idRes: Int?): (@Composable () -> Unit)? =
    if (idRes != null) {
        { Text(stringResource(idRes)) }
    } else null

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun <T : Parcelable> AlertDialog(
    state: DialogState<T>,
    title: Int? = null,
    text: Int? = null,
    negative: Int = R.string.no,
    positive: Int = R.string.yes,
    neutral: Int? = null,
) {
    AlertDialog(
        state = state,
        titleContent = text(title),
        textContent = text(text),
        buttonContent = {
            AlertDialogButtonsRow {
                TextButton(onClick = state::dismiss) {
                    Text(stringResource(negative).uppercase())
                }

                if (neutral != null) {
                    TextButton(onClick = state::neutral) {
                        Text(stringResource(neutral).uppercase())
                    }
                }

                TextButton(onClick = state::success) {
                    Text(stringResource(positive).uppercase())
                }
            }
        }
    )
}

@Composable
public fun <T : Parcelable> AlertDialog(
    state: DialogState<T>,
    iconContent: (@Composable () -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    textContent: (@Composable () -> Unit)? = null,
    buttonContent: (@Composable () -> Unit)? = null,
    shape: CornerBasedShape = DialogTokens.shape,
    containerColor: Color = DialogTokens.containerColor,
    tonalElevation: Dp = DialogTokens.TonalElevation,
    buttonContentColor: Color = DialogTokens.buttonContentColor,
    iconContentColor: Color = DialogTokens.iconContentColor,
    titleContentColor: Color = DialogTokens.titleContentColor,
    textContentColor: Color = DialogTokens.textContentColor,
) {
    CenterDialog(dialogState = state, shape = shape, containerColor = containerColor, elevation = tonalElevation) {
        AlertDialogContent(
            iconContent, titleContent, textContent, buttonContent, buttonContentColor, iconContentColor,
            titleContentColor, textContentColor,
        )
    }
}


@Composable
private fun AlertDialogContent(
    iconContent: (@Composable () -> Unit)?,
    titleContent: (@Composable () -> Unit)?,
    textContent: (@Composable () -> Unit)?,
    buttonContent: (@Composable () -> Unit)?,
    buttonContentColor: Color,
    iconContentColor: Color,
    titleContentColor: Color,
    textContentColor: Color,
) {
    Column(modifier = Modifier.horizontalInsetsPadding().padding(DialogPadding)) {
        if (iconContent != null) {
            Box(
                modifier = Modifier
                    .padding(IconPadding)
                    .align(Alignment.CenterHorizontally),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides iconContentColor,
                    iconContent
                )
            }
        }
        if (titleContent != null) {
            Box(
                modifier = Modifier
                    .padding(TitlePadding)
                    .align(if (iconContent == null) Alignment.Start else Alignment.CenterHorizontally),
            ) {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    ProvideTextStyle(DialogTokens.headlineFont, titleContent)
                }
            }
        }

        if (textContent != null) {
            Box(
                modifier = Modifier
                    .padding(TextPadding)
                    .align(Alignment.Start),
            ) {
                CompositionLocalProvider(LocalContentColor provides textContentColor) {
                    ProvideTextStyle(DialogTokens.supportingTextFont, textContent)
                }
            }
        }

        if (buttonContent != null) {
            Box(modifier = Modifier.align(Alignment.End)) {
                CompositionLocalProvider(LocalContentColor provides buttonContentColor) {
                    ProvideTextStyle(DialogTokens.actionLabelTextFont, buttonContent)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlertDialogButtonsRow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(ButtonsCrossAxisSpacing, Alignment.End),
        verticalArrangement = Arrangement.spacedBy(
            ButtonsMainAxisSpacing,
            Alignment.CenterVertically
        ),
        content = content
    )
}

public object DialogTokens {
    public val TonalElevation: Dp = 6.dp
    public val actionLabelTextFont: TextStyle
        @Composable get() = MaterialTheme.typography.labelLarge

    public val headlineFont: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall

    public val supportingTextFont: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium

    public val shape: CornerBasedShape
        @Composable get() = MaterialTheme.shapes.extraLarge

    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    public val iconContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.secondary

    public val titleContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    public val textContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    public val buttonContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary
}
