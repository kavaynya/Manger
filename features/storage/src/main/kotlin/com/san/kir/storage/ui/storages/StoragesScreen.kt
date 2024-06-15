package com.san.kir.storage.ui.storages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.san.kir.core.compose.CircleLogo
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.SwipeToDelete
import com.san.kir.core.compose.SwipeToDeleteDefaults
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.animateToDelayed
import com.san.kir.core.compose.animation.rememberDoubleAnimatable
import com.san.kir.core.compose.animation.rememberIntAnimatable
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.createDismissStates
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.format
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.OnEvent
import com.san.kir.core.utils.viewModel.ReturnEvents
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.MangaLogo
import com.san.kir.data.models.main.Storage
import com.san.kir.storage.R
import com.san.kir.storage.utils.StorageProgressBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val DurationDismissConfirmation = 1400
private val ImageSize = Dimensions.Image.bigger
private val ImageStartPadding = Dimensions.quarter
private val HorizontalItemPadding = Dimensions.default - ImageStartPadding
private val VerticalItemPadding = Dimensions.half
private val HorizontalItemContentPadding = Dimensions.half
private val DismissEndPadding = ImageSize + HorizontalItemPadding + HorizontalItemContentPadding
private val NoMangaTextSize = 14.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoragesScreen(
    navigateUp: () -> Unit,
    navigateToItem: (Long, SharedParams, Boolean) -> Unit,
) {
    val ctx = LocalContext.current

    val storageTitle = stringResource(R.string.storage)
    val sizeTemplate = stringResource(R.string.main_menu_storage_size_mb)

    val holder: StoragesStateHolder = stateHolder { StoragesViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val items by holder.items.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    holder.OnEvent { event ->
        if (event is StoragesEvent.ToStorage) {
            navigateToItem(event.id, event.params, event.hasUpdate)
        }
    }

    val size = rememberDoubleAnimatable()
    val count = rememberIntAnimatable()

    LaunchedEffect(state.size) { size.animateToDelayed(state.size) }
    LaunchedEffect(state.count) { count.animateToDelayed(state.count, 100) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val dismissStates = remember(items.size) { createDismissStates(items.size, density, scope) }

    val title by remember {
        derivedStateOf { storageTitle + " " + sizeTemplate.format(size.value.format()) }
    }
    val subtitle by remember {
        derivedStateOf {
            ctx.resources.getQuantityString(R.plurals.storage_subtitle, count.value, count)
        }
    }

    ScreenList(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = title,
            subtitle = subtitle,
            hasAction = state.background.current
        ),
        additionalPadding = Dimensions.quarter,
    ) {
        items(items.size, key = { i -> items[i].storage.id }) { index ->
            val item = items[index]
            ItemView(
                item = item.storage,
                manga = item.mangaLogo,
                storageSize = state.size,
                dismissState = dismissStates[index],
                sendAction = sendAction
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LazyItemScope.ItemView(
    item: Storage,
    manga: MangaLogo?,
    storageSize: Double,
    dismissState: SwipeToDismissBoxState,
    sendAction: (Action) -> Unit,
) {
    val params = rememberSharedParams()
    val sizeFullCounter = rememberDoubleAnimatable()
    val sizeReadCounter = rememberDoubleAnimatable()

    LaunchedEffect(item.sizeFull) {
        launch { sizeFullCounter.animateToDelayed(item.sizeFull) }
        launch { sizeReadCounter.animateToDelayed(item.sizeRead) }
    }

    SwipeToDelete(
        state = dismissState,
        modifier = Modifier
            .fillMaxWidth()
            .saveParams(params)
            .padding(HorizontalItemPadding, VerticalItemPadding)
            .animateItemPlacement(),
        resetText = R.string.clear,
        agreeText = R.string.yes,
        resetDesc = R.string.hold_yes_for_clear,
        durationDismissConfirmation = DurationDismissConfirmation,
        onSuccessDismiss = {}
    ) {
        Row(
            modifier = Modifier
                .clip(SwipeToDeleteDefaults.MainItemShape)
                .clickable {
                    manga?.let {
                        sendAction(
                            ReturnEvents(StoragesEvent.ToStorage(it.id, params, false))
                        )
                    }
                }
                .padding(vertical = Dimensions.quarter)
        ) {
            // Иконка манги, если для этой папки она еще есть
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = ImageStartPadding)
                    .size(ImageSize)
                    .align(Alignment.CenterVertically)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = SwipeToDeleteDefaults.MainItemShape
                    )
            ) {
                if (manga != null) {
                    CircleLogo(logoUrl = manga.logo, size = ImageSize)
                } else {
                    Text(
                        text = stringResource(R.string.storage_not_in_bd),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = NoMangaTextSize,
                        fontWeight = FontWeight.W400,
                        textAlign = TextAlign.Center,
                        lineHeight = NoMangaTextSize
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = HorizontalItemContentPadding),
                verticalArrangement = Arrangement.Center
            ) {
                // Название папки с мангой
                if (manga == null) {
                    Text(item.name, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                } else {
                    Text(manga.name, maxLines = 1, style = MaterialTheme.typography.labelSmall)
                    Text(
                        stringResource(R.string.folder_format, item.name),
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                }


                // Текстовая Информация о занимаемом месте
                UsedText(sizeFullCounter, storageSize)

                // Прогрессбар занимаемого места
                StorageProgressBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.quarter, end = Dimensions.default)
                        .height(Dimensions.smaller),
                    max = storageSize,
                    full = item.sizeFull,
                    read = item.sizeRead,
                )
            }
        }
    }
}


@Composable
private fun UsedText(sizeFull: Animatable<Double, AnimationVector1D>, storageSize: Double) {
    Text(
        stringResource(
            R.string.storage_manga_item_size_text,
            sizeFull.value.format(),
            if (storageSize != 0.0) (sizeFull.value / storageSize * 100).roundToInt() else 0
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.smallest),
        style = MaterialTheme.typography.bodyMedium
    )
}
