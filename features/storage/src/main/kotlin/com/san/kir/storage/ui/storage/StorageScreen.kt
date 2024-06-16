package com.san.kir.storage.ui.storage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.AlertDialog
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.animateToDelayed
import com.san.kir.core.compose.animation.rememberDoubleAnimatable
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.format
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.Storage
import com.san.kir.storage.R
import com.san.kir.storage.utils.StorageProgressBar
import com.san.kir.storage.utils.StorageReadColor
import com.san.kir.storage.utils.StorageShape
import com.san.kir.storage.utils.StorageTrackColor
import com.san.kir.storage.utils.StorageUsedColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StorageScreen(
    navigateUp: () -> Unit,
    mangaId: Long,
    hasUpdate: Boolean,
) {
    val holder: StorageStateHolder = stateHolder { StorageViewModel(mangaId, hasUpdate) }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    val dialogState = rememberDialogState<DeleteStatus>(onSuccess = {
        when (it) {
            DeleteStatus.Read -> sendAction(StorageAction.DeleteRead)
            DeleteStatus.All -> sendAction(StorageAction.DeleteAll)
        }
    })

    holder.OnEvent {
        when (it) {
            is StorageEvent.ShowDeleteDialog -> dialogState.show(it.mode)
        }
    }

    ScreenContent(
        scrollBehavior = null,
        additionalPadding = Dimensions.default,
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = state.mangaName,
            hasAction = state.background !is BackgroundState.None,
        ),
    ) {
        Content(
            background = state.background,
            storage = state.item,
            size = state.size,
            sendAction = sendAction
        )
    }

    AlertDialog(
        state = dialogState,
        text = R.string.delete_read_chapters_message,
        negative = R.string.no_no,
        positive = R.string.yes,
    )
}

@Composable
private fun Content(
    background: BackgroundState,
    storage: Storage,
    size: Double,
    sendAction: (Action) -> Unit,
) {
    val all = rememberDoubleAnimatable()
    val full = rememberDoubleAnimatable()
    val read = rememberDoubleAnimatable()

    LaunchedEffect(size) { all.animateTo(size) }
    LaunchedEffect(storage) {
        launch { full.animateToDelayed(storage.sizeFull, 300) }
        launch { read.animateToDelayed(storage.sizeRead) }
    }

    val hasReads by remember(storage) { derivedStateOf { storage.sizeRead > 0 } }
    val hasAny by remember(storage) { derivedStateOf { storage.sizeFull > 0 } }

    StorageProgressBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.ProgressBar.storage)
            .padding(vertical = Dimensions.half)
            .horizontalInsetsPadding(),
        max = all.value,
        full = full.value,
        read = read.value
    )

    // Строка с отображением всего занятого места
    StorageItem(StorageTrackColor, R.string.all_size_format, all)
    // Строка с отображением занятого места выбранной манги
    StorageItem(StorageUsedColor, R.string.manga_size_format, full)
    // Строка с отображение занятого места прочитанных глав выбранной манги
    StorageItem(StorageReadColor, R.string.read_size_format, read)

    // Кнопки очистки от манги появляющиеся только если есть, что удалять
    when (background) {
        BackgroundState.Load -> {}
        BackgroundState.None -> {
            // Удаление прочитанных глав
            AnimatedVisibility(hasReads) {
                DeleteItem(R.string.delete_read) {
                    sendAction(ReturnEvents(StorageEvent.ShowDeleteDialog(DeleteStatus.Read)))
                }
            }
            // Удаление содержимого папки
            AnimatedVisibility(hasAny) {
                DeleteItem(R.string.clear_folder) {
                    sendAction(ReturnEvents(StorageEvent.ShowDeleteDialog(DeleteStatus.All)))
                }
            }
        }

        BackgroundState.Deleting -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.deleting),
                    modifier = Modifier.padding(Dimensions.default)
                )
            }
        }
    }
}

@Composable
private fun StorageItem(color: Color, id: Int, value: Animatable<Double, AnimationVector1D>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = Dimensions.default)
                .horizontalInsetsPadding()
                .size(Dimensions.Image.storage)
                .background(color = color, shape = StorageShape)
        )
        Text(stringResource(id, value.value.format()))
    }
}

@Composable
private fun DeleteItem(id: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = Dimensions.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier
                .padding(end = Dimensions.default)
                .horizontalInsetsPadding()
                .size(Dimensions.Image.storage)
        )
        Text(stringResource(id))
    }
}
