package com.san.kir.chapters.ui.latest

import com.san.kir.core.utils.viewModel.StateHolder
import kotlinx.coroutines.flow.StateFlow

internal interface LatestStateHolder : StateHolder<LatestState> {
    val items: StateFlow<List<DateContainer>>
    val selection: StateFlow<SelectionState>
}
