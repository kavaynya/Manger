package com.san.kir.features.accounts.shikimori.logic.repo

import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.features.shikimori.logic.models.toFlowLibraryMangaItem
import com.san.kir.features.shikimori.logic.models.toFlowLibraryMangaItems
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

internal class LibraryItemRepository(
    private val mangaRepository: MangaRepository,
) : ItemsRepository {
    override fun loadItems() = mangaRepository.itemsWithChaptersCount.toFlowLibraryMangaItems()

    override fun loadItemById(id: Long) =
        mangaRepository.loadItemWithChaptersCount(id).toFlowLibraryMangaItem()

    override suspend fun items() = loadItems().first()

    override suspend fun itemById(id: Long) = loadItemById(id).firstOrNull()
}
