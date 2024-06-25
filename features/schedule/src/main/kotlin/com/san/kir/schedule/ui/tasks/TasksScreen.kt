package com.san.kir.schedule.ui.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.san.kir.core.compose.FabButton
import com.san.kir.core.compose.FabButtonHeight
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.schedule.utils.ItemContent

@Composable
internal fun TasksScreen(navigateToItem: (Long, SharedParams) -> Unit) {
    val holder: TasksStateHolder = stateHolder { TasksViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = bottomInsetsPadding(bottom = FabButtonHeight)
        ) {
            items(state.items.size, { index -> state.items[index].id }) { index ->
                val item = state.items[index]
                val navigate = rememberLambda<SharedParams> { navigateToItem(item.id, it) }

                ItemContent(
                    title = item.name,
                    subTitle = item.info,
                    checked = item.isEnabled,
                    onCheckedChange = {
                        sendAction(TasksAction.Update(item.id, item.isEnabled.not()))
                    },
                    onClick = navigate
                )
            }
        }

        FabButton(modifier = Modifier.align(Alignment.BottomEnd)) { navigateToItem(-1L, it) }
    }

}
