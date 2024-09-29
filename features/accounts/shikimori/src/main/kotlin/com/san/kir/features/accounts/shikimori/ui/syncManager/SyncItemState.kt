package com.san.kir.features.accounts.shikimori.ui.syncManager

import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.accounts.shikimori.logic.models.MangaItem

internal data class SyncItemState(
    val top: MangaItem,
    val bottom: List<MangaItem>,
    val status: Status = Status.None,
    val from: From = From.Library,
    val selectedId: Long = bottom.first().id
) {
    val id: Long = top.id
    val selectedIndex: Int = bottom.indexOfFirst { it.id == selectedId }

    enum class From { Library, Account }
    enum class Status { None, Applying, Error }

    val selectedRead: Int
        get() {
            if (
                (top is LibraryMangaItem && from == From.Library) ||
                (top is AccountMangaItem && from == From.Account)
            ) {
                return top.read
            }

            return bottom.first { it.id == selectedId }.read
        }

    val account: AccountMangaItem
        get() {
            if (top is AccountMangaItem) return top
            return bottom[selectedIndex] as AccountMangaItem
        }

    val library: LibraryMangaItem
        get() {
            if (top is LibraryMangaItem) return top
            return bottom[selectedIndex] as LibraryMangaItem
        }
}
