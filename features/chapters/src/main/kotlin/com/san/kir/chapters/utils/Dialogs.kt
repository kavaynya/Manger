package com.san.kir.chapters.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.window.DialogProperties
import com.san.kir.background.works.ChapterDeleteWorker
import com.san.kir.background.works.ReadChapterDelete
import com.san.kir.chapters.R
import com.san.kir.core.compose.AlertDialog
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.utils.navigation.DialogState
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.data.models.main.Manga

private fun dismissButton(onClick: () -> Unit) = @Composable {
    TextButton(onClick) {
        Text(
            stringResource(com.san.kir.core.compose.R.string.no)
                .toUpperCase(Locale.current)
        )
    }
}

private fun confirmButton(onClick: () -> Unit, onClose: () -> Unit) = @Composable {
    TextButton(
        onClick = {
            onClick()
            onClose()
        }
    ) {
        Text(
            stringResource(com.san.kir.core.compose.R.string.yes)
                .toUpperCase(Locale.current)
        )
    }
}

private fun text(idRes: Int?): @Composable (() -> Unit)? =
    if (idRes != null) {
        @Composable {
            Text(text = stringResource(id = idRes))
        }
    } else null

// Подготовленный шаблон диалога
@Composable
private fun PrepareAlertDialog(
    visible: Boolean,
    title: Int? = null,
    text: Int? = null,
    onClose: () -> Unit,
    onClick: () -> Unit,
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onClose,
            title = text(title),
            text = text(text),
            confirmButton = confirmButton(onClick, onClose),
            dismissButton = dismissButton(onClose)
        )
    }
}

// Диалог подтвержедения выделенных глав из БД
@Composable
internal fun FullDeleteChaptersAlertDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        title = R.string.warning,
        text = R.string.full_delete_message,
    )
}

// Диалог подтверждения удаления выделенных глав
@Composable
internal fun DeleteSelectedChaptersAlertDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        text = R.string.you_want_delete,
    )
}

@Composable
internal fun FullResetAlertDialog(resetDialogState: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = resetDialogState,
        title = R.string.warning,
        text = R.string.reset_reading_text,
        negative = R.string.dismiss,
        positive = R.string.start_again
    )
}


@Composable
internal fun DeleteChaptersDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        text = R.string.delete_read_chapters_message
    )
}


// Диалог оповещения о процессе очистки
@Composable
internal fun ProgressDeletingChaptersDialog(
    visible: Boolean,
    manga: Manga,
    onClose: () -> Unit,
) {
    if (visible) {
        // Индикатор выполнения операции
        var action by remember { mutableStateOf(true) }
        // Отображаемое сообщение в зависимости от статуса действия
        val message =
            if (action) R.string.deleting
            else com.san.kir.core.compose.R.string.ready


        AlertDialog(
            onDismissRequest = onClose,
            properties = DialogProperties(
                // Откючение возможности отменить диалог
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            confirmButton = {
                // Кнопка доступна после выполнения действия
                if (action.not())
                    TextButton(onClick = onClose) {
                        Text(stringResource(com.san.kir.catalog.R.string.close))
                    }
            },
            text = {
                Row(
                    modifier = Modifier.padding(Dimensions.default),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (action) CircularProgressIndicator()
                    Text(
                        stringResource(message),
                        modifier = Modifier.padding(horizontal = Dimensions.half)
                    )
                }
            }
        )

        LaunchedEffect(Unit) {
            ChapterDeleteWorker.addTask<ReadChapterDelete>(manga.id)
            ChapterDeleteWorker.workInfos()
                .collect { works -> action = works.any { it.state.isFinished.not() } }
        }
    }
}
