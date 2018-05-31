package com.san.kir.manger.components.listChapters

import android.support.v7.widget.RecyclerView
import com.san.kir.manger.R
import com.san.kir.manger.components.downloadManager.DownloadService
import com.san.kir.manger.components.main.Main
import com.san.kir.manger.room.dao.ChapterFilter
import com.san.kir.manger.room.models.Chapter
import com.san.kir.manger.room.models.Manga
import com.san.kir.manger.room.models.action
import com.san.kir.manger.room.models.toDownloadItem
import com.san.kir.manger.utils.ChapterStatus
import com.san.kir.manger.utils.RecyclerPresenter
import com.san.kir.manger.utils.RecyclerViewAdapterFactory
import com.san.kir.manger.utils.delChapters
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.startService
import org.jetbrains.anko.toast
import java.util.regex.Pattern


class ListChaptersRecyclerPresenter(val act: ListChaptersActivity) : RecyclerPresenter() {
    private val dao = Main.db.chapterDao
    private var adapter = RecyclerViewAdapterFactory.createSimple { ListChaptersItemView(act) }
    private var manga: Manga = Manga()
    private var backupCatalog = listOf<Chapter>()

    override fun into(recyclerView: RecyclerView) {
        super.into(recyclerView)
        recycler.adapter = adapter
    }

    fun setManga(
        manga: Manga = this.manga,
        filter: ChapterFilter
    ): Deferred<Deferred<Deferred<Unit>>> {
        this@ListChaptersRecyclerPresenter.manga = manga
        this@ListChaptersRecyclerPresenter.filter = filter
        return changeSort(manga.isAlternativeSort)
    }

    fun update() {
        changeSort(manga.isAlternativeSort)
    }

    fun changeSort(alternative: Boolean) = async {
        adapter.items = async {
            if (alternative) {
                dao.loadChapters(manga.unic).sortedWith(Comparator { arg1, arg2 ->
                    val reg = Pattern.compile("\\d+")
                    val matcher1 = reg.matcher(arg1.name)
                    val matcher2 = reg.matcher(arg2.name)

                    var numbers1 = listOf<String>()
                    var numbers2 = listOf<String>()

                    while (matcher1.find()) {
                        numbers1 += matcher1.group()
                    }

                    while (matcher2.find()) {
                        numbers2 += matcher2.group()
                    }

                    val prepare1 = String.format("%04d", numbers1[1].toInt(10))
                    val prepare2 = String.format("%04d", numbers2[1].toInt(10))

                    val finishNumber1 = "${numbers1.first()}$prepare1".toInt(10)
                    val finishNumber2 = "${numbers2.first()}$prepare2".toInt(10)

                    finishNumber1 - finishNumber2
                })
            } else {
                dao.loadChapters(manga.unic)
            }
        }.await()
        backupCatalog = adapter.items

        changeOrder(filter)
    }

    private var filter = ChapterFilter.NOT_READ_DESC
    fun changeOrder(filter: ChapterFilter) = async {
        this@ListChaptersRecyclerPresenter.filter = filter
        adapter.items = when (filter) {
            ChapterFilter.ALL_READ_ASC -> backupCatalog
            ChapterFilter.NOT_READ_ASC -> backupCatalog.filter { !it.isRead }
            ChapterFilter.IS_READ_ASC -> backupCatalog.filter { it.isRead }
            ChapterFilter.ALL_READ_DESC -> backupCatalog.reversed()
            ChapterFilter.NOT_READ_DESC -> backupCatalog.filter { !it.isRead }.reversed()
            ChapterFilter.IS_READ_DESC -> backupCatalog.filter { it.isRead }.reversed()
        }
        async(UI) {
            adapter.notifyDataSetChanged()
        }
    }

    fun toggleSelection(position: Int) {
        adapter.selectedItems[position] = !adapter.selectedItems[position]
        adapter.notifyItemChanged(position)
    }

    val selectedCount get() = adapter.selectedItems.filter { it }.size

    fun deleteSelectedItems() = async {
        var count = 0
        forSelection { i ->
            val chapter = items[i]
            if (chapter.action == ChapterStatus.DELETE) {
                delChapters(chapter)
                count++
            }
        }
        async(UI) {
            if (count == 0) {
                act.toast(R.string.list_chapters_selection_del_error)
            } else {
                act.toast(R.string.list_chapters_selection_del_ok)
            }
        }
    }

    fun downloadSelectedItems() = async {
        forSelection { i ->
            val chapter = items[i]
            // для каждого выделенный элемент
            if (chapter.action == ChapterStatus.DOWNLOADABLE) {
                val item = chapter.toDownloadItem()
                act.startService<DownloadService>("item" to item)
            }
        }
    }

    fun downloadNextNotReadChapter() = async(UI) {
        val job = async {
            val chapter = dao.loadChaptersNotReadAsc(manga.unic)
                .first { it.action == ChapterStatus.DOWNLOADABLE }
            act.startService<DownloadService>("item" to chapter.toDownloadItem())
        }

        job.await()

        changeOrder(filter)
    }

    fun downloadAllNotReadChapters() = async(UI) {
        val job = async {
            dao.loadChaptersNotReadAsc(manga.unic)
                .filter { it.action == ChapterStatus.DOWNLOADABLE }
                .onEach { chapter ->
                    act.startService<DownloadService>("item" to chapter.toDownloadItem())
                }
                .size
        }

        val count = job.await()

        if (count == 0)
            act.toast(R.string.list_chapters_selection_load_error)
        else
            act.toast(act.getString(R.string.list_chapters_selection_load_ok, count))

        changeOrder(filter)
    }

    fun downloadAllChapters() = async(UI) {
        val job = async {
            dao.loadChaptersAllAsc(manga.unic)
                .filter { it.action == ChapterStatus.DOWNLOADABLE }
                .onEach { chapter ->
                    act.startService<DownloadService>("item" to chapter.toDownloadItem())
                }
                .size
        }

        val count = job.await()
        if (count == 0)
            act.toast(R.string.list_chapters_selection_load_error)
        else
            act.toast(act.getString(R.string.list_chapters_selection_load_ok, count))
        changeOrder(filter)
    }

    fun setRead(isReading: Boolean) = async {
        forSelection { i ->
            // Для всех выделеных элементов
            items[i].let { chapter ->
                chapter.isRead = isReading
                dao.update(chapter)
            }
        }
        changeOrder(filter)
    }

    fun removeSelection() = async(UI) {
        repeat(adapter.itemCount) { i ->
            adapter.selectedItems[i] = false
            adapter.notifyItemChanged(i)
        }
    }

    fun selectAll() =
        repeat(adapter.itemCount) { i ->
            adapter.selectedItems[i] = true
            adapter.notifyItemChanged(i)
        }

    fun selectPrev() {
        // Выделить предидущие элементы
        val selectedSize = adapter.selectedItems.filter { it }.size
        if (selectedSize == 1) { // Работает только для одного выделенного элемента
            adapter.selectedItems.forEachIndexed { index, b ->
                if (b) {
                    repeat(adapter.selectedItems.size) { i ->
                        if (i > index) {
                            adapter.selectedItems[i] = true
                            adapter.notifyItemChanged(i)
                        }
                    }
                }
            }
        }
    }

    fun selectNext() {
        val selectedSize = adapter.selectedItems.filter { it }.size
        if (selectedSize == 1) { // Работает только для одного выделенного элемента
            adapter.selectedItems.forEachIndexed { index, b ->
                if (b) {
                    repeat(adapter.selectedItems.size) { i ->
                        if (i < index) {
                            adapter.selectedItems[i] = true
                            adapter.notifyItemChanged(i)
                        }
                    }
                }
            }
        }
    }

    fun fullDeleteSelectedItems() = async {
        forSelection { i ->
            items[i].let { chapter ->
                dao.delete(chapter)
                backupCatalog -= chapter
            }
        }
        changeOrder(filter)
    }

    //* Приватные функции *//*
    private fun forSelection(block: RecyclerViewAdapterFactory.RecyclerViewAdapter<Chapter>.(Int) -> Unit) {
        val copySelectedItems = adapter.selectedItems.copyOf()
        copySelectedItems.forEachIndexed { index, b ->
            if (b) {
                adapter.block(index)
            }
        }
    }
}