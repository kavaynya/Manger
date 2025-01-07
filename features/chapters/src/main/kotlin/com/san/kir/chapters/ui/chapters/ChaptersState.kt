package com.san.kir.chapters.ui.chapters

import androidx.compose.ui.graphics.vector.ImageVector
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.main.Manga
import com.san.kir.data.models.main.SimplifiedChapter
import com.san.kir.data.models.utils.ChapterFilter
import io.ktor.http.HttpStatusCode

internal data class ChaptersState(
    val manga: Manga = Manga(),
    val backgroundAction: Boolean = false,
    val showTitle: Boolean = true,
    val chapterFilter: ChapterFilter = ChapterFilter.ALL_READ_ASC,
) : ScreenState {

    val error = when (manga.lastUpdateError) {
        null -> ErrorState.None
        HttpStatusCode.NotFound.value.toString() -> ErrorState.NotFound
        else -> ErrorState.Other
    }

    override fun toString(): String {
        return "ChaptersState(manga=${manga.name}, backgroundAction=$backgroundAction, showTitle=$showTitle, chapterFilter=$chapterFilter)"
    }
}

internal data class SelectableItem(val chapter: SimplifiedChapter, val selected: Boolean)

internal data class BackgroundActions(
    val updateManga: Boolean = false,
    val updateItems: Boolean = true,
    val updatePages: Boolean = false,
) {
    val result = updateManga || updateItems || updatePages
}

internal data class Items(
    val items: List<SelectableItem> = emptyList(),
    val memoryPagesCounts: Map<Long, Int> = emptyMap(),
    val count: Int = items.count(),
    val readCount: Int = items.count { it.chapter.isRead },
) {
    val hasReadingChapters: Boolean = items.any { it.chapter.progress > 1 || it.chapter.isRead }
}

internal data class SelectionMode(
    val count: Int = 0,
    val hasReading: Int = 0,
    val canSetRead: Int = 0,
    val canSetUnread: Int = 0,
    val canRemovePages: Int = 0,
    val remain: Int = 0,
    val aboveCount: Int = 0,
    val belowCount: Int = 0,
) {
    val enabled: Boolean = count > 0
}

internal enum class ErrorState { None, NotFound, Other; }

internal sealed interface NextChapter {
    data object None : NextChapter

    data object Loading : NextChapter
    sealed class Ok(val id: Long, val name: String) : NextChapter {
        class Continue(id: Long, name: String) : Ok(id, name)
        class First(id: Long, name: String) : Ok(id, name)
        class Single(id: Long, name: String) : Ok(id, name)
    }
}

internal data class BottomSortHelper(
    val icon: ImageVector,
    val action: () -> Unit,
    val checkEnable: (ChapterFilter) -> Boolean
)
