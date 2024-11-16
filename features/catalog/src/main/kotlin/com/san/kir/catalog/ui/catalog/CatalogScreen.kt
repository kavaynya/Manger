package com.san.kir.catalog.ui.catalog

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ThumbsUpDown
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.twotone.Cancel
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.san.kir.catalog.R
import com.san.kir.catalog.utils.ListItem
import com.san.kir.core.compose.AlertDialog
import com.san.kir.core.compose.DataIconHelper
import com.san.kir.core.compose.DataTextHelper
import com.san.kir.core.compose.DefaultBottomBar
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.HorizontalIconRadioGroup
import com.san.kir.core.compose.HorizontalTextRadioGroup
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.RotateToggleButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.StartAnimatedVisibility
import com.san.kir.core.compose.animation.animateToDelayed
import com.san.kir.core.compose.animation.rememberIntAnimatable
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.endInsetsPadding
import com.san.kir.core.compose.startInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.compose.topInsetsPadding
import com.san.kir.core.utils.add
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.DialogState
import com.san.kir.core.utils.navigation.EmptyDialogData
import com.san.kir.core.utils.navigation.rememberDialogState
import com.san.kir.core.utils.navigation.show
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.catalog.SiteCatalogElement
import com.san.kir.data.models.catalog.toFullItem
import java.net.URLDecoder

private val SortBarPadding = Dimensions.default
private val IconSize = DpSize(
    width = 24.dp + Dimensions.default * 2,
    height = 24.dp + Dimensions.half * 2,
)
private val SortBarHeightWithPadding = IconSize.height + SortBarPadding * 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogScreen(
    navigateUp: () -> Unit,
    navigateToInfo: (SiteCatalogElement, SharedParams) -> Unit,
    navigateToAdd: (String, SharedParams) -> Unit,
    catalogName: String,
) {
    val holder: CatalogStateHolder = stateHolder { CatalogViewModel(catalogName) }
    val filters by holder.filters.collectAsStateWithLifecycle()
    val background by holder.backgroundWork.collectAsStateWithLifecycle()
    val filterState by holder.filterState.collectAsStateWithLifecycle()
    val sortState by holder.sortState.collectAsStateWithLifecycle()
    val items by holder.items.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val reloadDialog = rememberDialogState<EmptyDialogData> {
        sendAction(CatalogAction.UpdateContent)
    }
    val cancelDialog = rememberDialogState<EmptyDialogData> {
        sendAction(CatalogAction.CancelUpdateContent)
    }

    var enableSearch by rememberSaveable { mutableStateOf(false) }
    val query: ((String) -> Unit)? = remember(enableSearch) {
        if (enableSearch) {
            { if (it != filterState.search) sendAction(CatalogAction.Search(it)) }
        } else null
    }
    val itemCount = rememberIntAnimatable()
    LaunchedEffect(items.size) {
        itemCount.snapTo(itemCount.value)
        itemCount.animateToDelayed(items.size, duration = 400)
    }

    ScreenList(
        additionalPadding = Dimensions.zero,
        contentPadding = bottomInsetsPadding(
            top = Dimensions.half,
            bottom = SortBarHeightWithPadding
        ),
        drawerState = drawerState,
        topBar = topBar(
            title = "${catalogName}: ${itemCount.value}",
            navigationButton =
            if (filters.isNotEmpty()) {
                NavigationButton.Scaffold(drawerState, filterState.hasSelectedFilters)
            } else {
                NavigationButton.Back(navigateUp)
            },
            actions = {
                MenuIcon(
                    icon = Icons.Default.Search,
                    hasNotify = !enableSearch && filterState.search.isNotEmpty()
                ) { enableSearch = !enableSearch }

                FromEndToEndAnimContent(background.updateCatalogs) {
                    if (!it) {
                        MenuIcon(Icons.Outlined.SyncAlt, onClick = reloadDialog::show)
                    } else {
                        MenuIcon(Icons.TwoTone.Cancel, onClick = cancelDialog::show)
                    }
                }
            },
            onSearchTextChange = query,
            hasAction = background.currentState,
            progressAction = background.progress,
            initSearchText = filterState.search
        ),
        drawerContent =
        if (filters.isNotEmpty()) {
            { DrawerContent(filters, filterState.hasSelectedFilters, sendAction) }
        } else {
            null
        },
        bottomContent = { BottomBar(sortState, sendAction) },
    ) {
        items(items, key = { it.id }) { item ->
            ListItem(
                item, item.statusEdition,
                toAdd = {params -> navigateToAdd(item.link, params)} ,
                toInfo = { params -> navigateToInfo(item.toFullItem(), params) },
                updateItem = { sendAction(CatalogAction.UpdateManga(it)) }
            )
        }
    }

    ReloadDialog(reloadDialog)
    CancelDialog(cancelDialog)
}

// Нижняя панель с кнопками сортировки списка
@Composable
private fun BottomBar(
    sort: SortState,
    sendAction: (CatalogAction) -> Unit,
) {
    val dataHelpers by remember {
        derivedStateOf {
            val list = listOf(
                DataIconHelper(Icons.Default.SortByAlpha, SortType.Name),
                DataIconHelper(Icons.Default.DateRange, SortType.Date),
            )

            if (sort.hasPopulateSort) {
                list.add(DataIconHelper(Icons.Default.ThumbsUpDown, SortType.Pop))
            } else {
                list
            }
        }
    }

    DefaultBottomBar(modifier = Modifier.endInsetsPadding(right = Dimensions.default)) {

        RotateToggleButton(icon = Icons.AutoMirrored.Filled.Sort, state = sort.reverse) {
            sendAction(CatalogAction.Reverse)
        }

        HorizontalIconRadioGroup(
            dataHelpers = dataHelpers,
            initialValue = sort.type,
            modifier = Modifier.padding(Dimensions.middle)
        ) {
            sendAction(CatalogAction.ChangeSort(it))
        }
    }
}


// Боковое меню
@Composable
private fun DrawerContent(
    filters: List<Filter>,
    hasSelectedFilters: Boolean,
    sendAction: (CatalogAction) -> Unit
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }

    // Списки фильтров
    Column {
        Crossfade(
            targetState = currentIndex,
            modifier = Modifier.weight(1f),
            label = "",
        ) { pageIndex ->
            val currentFilter = filters[pageIndex]

            LazyColumn(contentPadding = topInsetsPadding()) {
                itemsIndexed(currentFilter.items, key = { i, _ -> i }) { index, item ->
                    // Строка списка
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sendAction(CatalogAction.ChangeFilter(currentFilter.type, index)) }
                            .startInsetsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.state,
                            onCheckedChange = { sendAction(CatalogAction.ChangeFilter(currentFilter.type, index)) },
                        )
                        Text(URLDecoder.decode(item.name, "UTF-8"))
                    }
                }
            }
        }

        // Горизонтальная линия
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.smallest)
                .clip(RectangleShape)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        )

        // Переключатели вкладок доступных фильтров
        Row(
            modifier = Modifier.fillMaxWidth().startInsetsPadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            HorizontalTextRadioGroup(
                dataHelpers = filters.mapIndexed { index, filter ->
                    DataTextHelper(filter.type.stringId, index)
                },
                initialValue = currentIndex,
                modifier = Modifier
                    .padding(Dimensions.default)
                    .weight(1f)
            ) {
                currentIndex = it
            }

            StartAnimatedVisibility(hasSelectedFilters) {
                FilledIconButton(
                    onClick = { sendAction(CatalogAction.ClearFilters) },
                    modifier = Modifier.padding(end = Dimensions.default)
                ) {
                    Icon(Icons.Default.Clear, null)
                }
            }
        }
    }
}

// Диалог подтверждения на обновление каталога
@Composable
private fun ReloadDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        title = R.string.warning,
        text = R.string.update_catalog,
        negative = R.string.not_agree,
        positive = R.string.agree
    )
}

// Диалог отмены обновления каталога
@Composable
private fun CancelDialog(state: DialogState<EmptyDialogData>) {
    AlertDialog(
        state = state,
        title = R.string.warning,
        text = R.string.cancel_update_catalog,
        negative = R.string.not_agree,
        positive = R.string.agree
    )
}
