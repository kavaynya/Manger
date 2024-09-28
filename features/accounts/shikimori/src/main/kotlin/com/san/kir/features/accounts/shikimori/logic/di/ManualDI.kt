package com.san.kir.features.accounts.shikimori.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.accountMangaRepository
import com.san.kir.data.accountsRepository
import com.san.kir.data.chapterRepository
import com.san.kir.data.mangaRepository
import com.san.kir.features.shikimori.logic.models.MangaItem
import com.san.kir.features.shikimori.logic.repo.AccountItemRepository
import com.san.kir.features.shikimori.logic.repo.AuthRepository
import com.san.kir.features.shikimori.logic.repo.ItemsRepository
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import okhttp3.Cache
import java.io.File

private const val CACHE_SIZE = 5L * 1024 * 1024

private fun ManualDI.cache(): Cache {
    val cacheDir = File(application.cacheDir, "shiki")
    return Cache(cacheDir, CACHE_SIZE)
}

private fun ManualDI.client(): InternetClient = InternetClient(cache(), json)

internal fun ManualDI.authRepository(): AuthRepository {
    return AuthRepository(client(), accountsRepository())
}

internal fun ManualDI.libraryItemRepository(): LibraryItemRepository {
    return LibraryItemRepository(mangaRepository())
}

internal fun ManualDI.accountItemRepository(accountId: Long): AccountItemRepository {
    return AccountItemRepository(
        accountId,
        accountsRepository(),
        accountMangaRepository(),
        client()
    )
}

internal fun <T : MangaItem> ManualDI.syncManager(
    accountId: Long,
    opposite: ItemsRepository
): ISyncManager<T> {
    return SyncManager(accountItemRepository(accountId), chapterRepository(), opposite)
}
