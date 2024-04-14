package com.san.kir.chapters.logic.utils

import com.san.kir.chapters.ui.chapters.Items
import com.san.kir.chapters.ui.chapters.SelectableItem
import com.san.kir.data.models.utils.ChapterFilter
import com.san.kir.data.models.base.Manga
import com.san.kir.data.models.extend.SimplifiedChapter
import com.san.kir.data.models.extend.SimplifiedChapterComparator
import kotlinx.collections.immutable.toPersistentList

internal object SelectionHelper {
    private val simplifiedChapterComparator by lazy { SimplifiedChapterComparator() }

    fun above(old: Items): Items {
        val firstSelected = old.items.indexOfFirst { it.selected }

        val newItems = old
            .items
            .mapIndexed { index, item -> item.copy(selected = firstSelected > index) }
            .toPersistentList()

        return old.copy(items = newItems)
    }

    fun below(old: Items): Items {
        val firstSelected = old.items.indexOfFirst { it.selected }

        val newItems = old
            .items
            .mapIndexed { index, item -> item.copy(selected = firstSelected < index) }
            .toPersistentList()

        return old.copy(items = newItems)
    }

    fun all(old: Items): Items {
        val newItems = old
            .items
            .map { item -> item.copy(selected = true) }
            .toPersistentList()

        return old.copy(items = newItems)
    }

    fun clear(old: Items): Items {
        val newItems = old
            .items
            .map { item -> item.copy(selected = false) }
            .toPersistentList()

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
        filter: com.san.kir.data.models.utils.ChapterFilter,
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
            items = items.toPersistentList(),
            count = list.size,
            readCount = list.count { it.isRead })
    }

    private fun List<SimplifiedChapter>.applyFilter(
        filter: com.san.kir.data.models.utils.ChapterFilter,
        manga: Manga
    ): List<SimplifiedChapter> {
        var list = this
        if (manga.isAlternativeSort) {
            list = list.sortedWith(simplifiedChapterComparator)
        }
        return when (filter) {
            com.san.kir.data.models.utils.ChapterFilter.ALL_READ_ASC -> list
            com.san.kir.data.models.utils.ChapterFilter.NOT_READ_ASC -> list.filterNot { it.isRead }
            com.san.kir.data.models.utils.ChapterFilter.IS_READ_ASC -> list.filter { it.isRead }
            com.san.kir.data.models.utils.ChapterFilter.ALL_READ_DESC -> list.reversed()
            com.san.kir.data.models.utils.ChapterFilter.NOT_READ_DESC -> list.filterNot { it.isRead }.reversed()
            com.san.kir.data.models.utils.ChapterFilter.IS_READ_DESC -> list.filter { it.isRead }.reversed()
        }
    }
}
