package com.san.kir.chapters.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import com.san.kir.chapters.R
import com.san.kir.chapters.ui.chapters.ChaptersAction
import com.san.kir.chapters.ui.chapters.ChaptersEvent
import com.san.kir.chapters.ui.chapters.ErrorState
import com.san.kir.chapters.ui.chapters.NextChapter
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.FromTopToTopAnimContent
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.contentColorBy
import com.san.kir.core.compose.endInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.intToComposeColor
import com.san.kir.core.compose.isLandscape
import com.san.kir.core.compose.isPortrait
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.compose.startInsetsPadding
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ReturnEvents

// Простая страница с минимум возможностей для быстрого продолжения чтения
@Composable
internal fun AboutPageContent(
    nextChapter: NextChapter,
    color: Int,
    logo: String,
    readCount: Int,
    count: Int,
    hasReading: Boolean,
    errorState: ErrorState,
    sendAction: (Action) -> Unit,
) {
    val defaultContainerColor = MaterialTheme.colorScheme.primary
    val containerColor = intToComposeColor(color, defaultContainerColor)
    val contentColor = contentColorBy(containerColor)

    Box(modifier = Modifier.fillMaxSize()) {
        // Большое лого манги
        Image(
            rememberImage(logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = Dimensions.half, top = Dimensions.half, end = Dimensions.half)
                .clip(RoundedCornerShape(topStart = Dimensions.big, topEnd = Dimensions.big))
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {

            if (isPortrait()) {
                ResetButton(
                    hasReading,
                    containerColor,
                    contentColor,
                    sendAction,
                    Modifier.align(Alignment.End)
                )
            }

            FromTopToTopAnimContent(
                targetState = errorState,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(
                            topStart = Dimensions.default,
                            topEnd = Dimensions.default
                        )
                    ),
            ) { state ->
                when (state) {
                    ErrorState.None -> Unit

                    ErrorState.NotFound -> {
                        ErrorContainer(
                            text = R.string.no_update_error,
                            button = R.string.go,
                            onClick = { sendAction(ReturnEvents(ChaptersEvent.ToGlobalSearch)) }
                        )
                    }

                    ErrorState.Other -> {
                        ErrorContainer(
                            text = R.string.last_update_error,
                            button = R.string.ok,
                            onClick = { sendAction(ChaptersAction.ResetError) },
                        )
                    }
                }
            }

            // информация о прочитанных главах
            Text(
                stringResource(
                    R.string.you_reading_format,
                    readCount,
                    pluralStringResource(R.plurals.chapters, readCount),
                    count
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerColor)
                    .horizontalInsetsPadding(Dimensions.default),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = contentColor,
            )

            Row {
                // Продолжение чтения
                Button(
                    onClick = {
                        if (nextChapter is NextChapter.Ok) {
                            sendAction(ReturnEvents(ChaptersEvent.ToViewer(nextChapter.id)))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Dimensions.default, vertical = Dimensions.half)
                        .startInsetsPadding()
                        .bottomInsetsPadding(),
                    enabled = nextChapter is NextChapter.Ok,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    ),
                    contentPadding = PaddingValues()
                ) {
                    when (nextChapter) {
                        NextChapter.None -> ButtonText(stringResource(R.string.not_continue_reading))
                        NextChapter.Loading -> CircularProgressIndicator()

                        is NextChapter.Ok -> {
                            val textId = when (nextChapter) {
                                is NextChapter.Ok.Continue -> R.string.continue_reading_format
                                is NextChapter.Ok.First -> R.string.start_reading_format
                                is NextChapter.Ok.Single -> R.string.single_reading_format
                            }
                            ButtonText(stringResource(textId, nextChapter.name))
                        }
                    }
                }

                if (isLandscape()) {
                    ResetButton(
                        hasReading,
                        containerColor,
                        contentColor,
                        sendAction,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonText(content: String) {
    val locale = Locale.current
    Text(
        text = content.toUpperCase(locale),
        modifier = Modifier.padding(Dimensions.default),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ResetButton(
    hasReading: Boolean,
    containerColor: Color,
    contentColor: Color,
    sendAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = { sendAction(ReturnEvents(ChaptersEvent.ShowFullResetDialog)) },
        modifier = modifier.endInsetsPadding(Dimensions.default),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        )
    ) {
        Icon(Icons.Default.LockReset, "")
    }
}

@Composable
private fun ErrorContainer(
    text: Int,
    button: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.horizontalInsetsPadding(Dimensions.default),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
        )

        DefaultSpacer()

        Text(
            text = stringResource(button),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .clip(DefaultRoundedShape)
                .background(MaterialTheme.colorScheme.error, DefaultRoundedShape)
                .clickable(onClick = onClick)
                .padding(Dimensions.half),
        )
    }
}
