package com.san.kir.features.accounts.shikimori.logic.repo

import com.san.kir.features.accounts.shikimori.logic.models.MangaItem
import kotlinx.coroutines.flow.Flow

internal interface ItemsRepository {

    suspend fun itemById(id: Long): MangaItem?

    suspend fun items(): List<MangaItem>

    fun loadItemById(id: Long): Flow<MangaItem?>

    fun loadItems(): Flow<List<MangaItem>>

}
