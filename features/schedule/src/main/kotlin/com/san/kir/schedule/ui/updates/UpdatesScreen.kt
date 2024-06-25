package com.san.kir.schedule.ui.updates

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.schedule.R
import com.san.kir.schedule.utils.ItemContent

@Composable
internal fun UpdatesScreen() {
    val holder: UpdatesStateHolder = stateHolder { UpdatesViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = bottomInsetsPadding(),
    ) {
        items(state.items.size, { index -> state.items[index].id }) { index ->
            val item = state.items[index]

            ItemContent(
                title = item.name,
                subTitle = stringResource(R.string.category_format, item.category),
                checked = item.update,
                onCheckedChange = { sendAction(UpdatesAction.Update(item.id, it)) }
            )
        }
    }
}
