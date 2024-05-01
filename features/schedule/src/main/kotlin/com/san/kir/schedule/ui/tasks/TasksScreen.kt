package com.san.kir.schedule.ui.tasks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.schedule.utils.ItemContent

@Composable
internal fun TasksScreen(
    navigateToItem: (Long, SharedParams) -> Unit,
) {
    val holder: TasksStateHolder = stateHolder { TasksViewModel() }
    val state by holder.state.collectAsState()

    val sendEvent = rememberLambda { event: TasksEvent -> holder.sendAction(event) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = bottomInsetsPadding()
    ) {
        items(state.items.size, { index -> state.items[index].id }) { index ->
            val item = state.items[index]

            ItemContent(
                title = item.name,
                subTitle = item.info,
                checked = item.isEnabled,
                onCheckedChange = { sendEvent(TasksEvent.Update(item.id, item.isEnabled.not())) },
                onClick = { navigateToItem(item.id, it) }
            )
        }
    }
}
