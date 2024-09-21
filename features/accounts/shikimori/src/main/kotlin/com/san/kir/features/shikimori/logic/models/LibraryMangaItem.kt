package com.san.kir.features.shikimori.logic.models

import com.san.kir.data.models.main.MangaWithChaptersCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal data class LibraryMangaItem(
    override val id: Long = 0L,
    override val name: String = "",
    override val logo: String = "",
    override val description: String = "",
    val sort: Boolean = false,
    override val read: Int = 0,
    override val all: Int = 0
) : MangaItem

internal fun MangaWithChaptersCount.toLibraryMangaItem(): LibraryMangaItem {
    return LibraryMangaItem(id, name, logo, description, sort, read, all)
}

internal fun List<MangaWithChaptersCount>.toLibraryMangaItems(): List<LibraryMangaItem> {
    return this.map(MangaWithChaptersCount::toLibraryMangaItem)
}

internal fun Flow<MangaWithChaptersCount?>.toFlowLibraryMangaItem(): Flow<LibraryMangaItem> {
    return mapNotNull { it?.toLibraryMangaItem() }
}

internal fun Flow<List<MangaWithChaptersCount>>.toFlowLibraryMangaItems(): Flow<List<LibraryMangaItem>> {
    return map(List<MangaWithChaptersCount>::toLibraryMangaItems)
}
