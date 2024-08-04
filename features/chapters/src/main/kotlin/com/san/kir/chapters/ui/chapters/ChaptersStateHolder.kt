package com.san.kir.chapters.ui.chapters

import com.san.kir.core.utils.viewModel.StateHolder
import kotlinx.coroutines.flow.StateFlow

internal interface ChaptersStateHolder : StateHolder<ChaptersState> {
    val itemsContent: StateFlow<Items>
    val nextChapter: StateFlow<NextChapter>
    val selectionMode: StateFlow<SelectionMode>
}
