package com.san.kir.chapters.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.luminance
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
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.rememberImage
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
    val defaultContentColor = MaterialTheme.colorScheme.onPrimary

    val containerColor = if (color != 0) Color(color) else null
    val contentColor = containerColor?.let { color ->
        if (color.luminance() > 0.5f) Color.Black else Color.White
    }

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

            StartAnimatedVisibility(hasReading, modifier = Modifier.align(Alignment.End)) {
                FilledIconButton(
                    onClick = { sendAction(ReturnEvents(ChaptersEvent.ShowFullResetDialog)) },
                    modifier = Modifier.padding(Dimensions.default),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = containerColor ?: defaultContainerColor,
                        contentColor = contentColor ?: defaultContentColor,
                    )
                ) {
                    Icon(Icons.Default.LockReset, "")
                }
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
                    ErrorState.None -> {

                    }

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
                    .background(containerColor ?: defaultContainerColor)
                    .horizontalInsetsPadding(Dimensions.default),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            // Продолжение чтения
            Button(
                onClick = {
                    if (nextChapter is NextChapter.Ok) {
                        sendAction(ReturnEvents(ChaptersEvent.ToViewer(nextChapter.id)))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.default)
                    .horizontalInsetsPadding()
                    .bottomInsetsPadding(),
                enabled = nextChapter is NextChapter.Ok,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor ?: defaultContainerColor,
                    contentColor = contentColor ?: defaultContentColor,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                )
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
