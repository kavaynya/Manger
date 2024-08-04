package com.san.kir.chapters.logic.utils

import com.san.kir.chapters.ui.chapters.Items
import com.san.kir.chapters.ui.chapters.SelectableItem
import com.san.kir.core.utils.set
import com.san.kir.data.models.main.Manga
import com.san.kir.data.models.main.SimplifiedChapter
import com.san.kir.data.models.utils.ChapterComparator
import com.san.kir.data.models.utils.ChapterFilter


internal object SelectionHelper {
    private val chapterComparator by lazy { ChapterComparator() }

    fun above(old: Items): Items {
        val firstSelected = old.items.indexOfFirst { it.selected }

        val newItems = old
            .items
            .mapIndexed { index, item -> item.copy(selected = firstSelected >= index) }

        return old.copy(items = newItems)
    }

    fun below(old: Items): Items {
        val firstSelected = old.items.indexOfFirst { it.selected }

        val newItems = old
            .items
            .mapIndexed { index, item -> item.copy(selected = firstSelected <= index) }

        return old.copy(items = newItems)
    }

    fun all(old: Items): Items {
        val newItems = old
            .items
            .map { item -> item.copy(selected = true) }

        return old.copy(items = newItems)
    }

    fun clear(old: Items): Items {
        val newItems = old
            .items
            .map { item -> item.copy(selected = false) }

        return old.copy(items = newItems)
    }

    fun change(old: Items, index: Int): Items {
        val changedItem = old.items[index]

        val newItems = old
            .items
            .set(index, changedItem.copy(selected = changedItem.selected.not()))

        return old.copy(items = newItems)
    }

    fun update(
        old: Items,
        list: List<SimplifiedChapter>,
        filter: ChapterFilter,
        manga: Manga
    ): Items {
        val newItems = list.applyFilter(filter, manga)

        val items =
            if (newItems.size != old.items.size) {
                newItems
                    .map { SelectableItem(it, false) }
            } else {
                newItems
                    .zip(old.items)
                    .map { (chapter, item) -> item.copy(chapter = chapter) }
            }

        return Items(
            items = items,
            count = list.size,
            readCount = list.count { it.isRead },
        )
    }

    private fun List<SimplifiedChapter>.applyFilter(
        filter: ChapterFilter,
        manga: Manga
    ): List<SimplifiedChapter> {
        var list = this
        if (manga.isAlternativeSort) {
            list = list.sortedWith(chapterComparator)
        }
        return when (filter) {
            ChapterFilter.ALL_READ_ASC -> list
            ChapterFilter.NOT_READ_ASC -> list.filterNot { it.isRead }
            ChapterFilter.IS_READ_ASC -> list.filter { it.isRead }
            ChapterFilter.ALL_READ_DESC -> list.reversed()
            ChapterFilter.NOT_READ_DESC -> list.filterNot { it.isRead }.reversed()
            ChapterFilter.IS_READ_DESC -> list.filter { it.isRead }.reversed()
        }
    }
}
