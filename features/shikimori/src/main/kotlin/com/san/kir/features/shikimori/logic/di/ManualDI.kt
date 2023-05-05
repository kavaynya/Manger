package com.san.kir.features.shikimori.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterDao
import com.san.kir.data.settingsDao
import com.san.kir.data.shikimoriDao
import com.san.kir.features.shikimori.logic.SyncManager
import com.san.kir.features.shikimori.logic.repo.ChapterRepository
import com.san.kir.features.shikimori.logic.repo.LibraryItemRepository
import com.san.kir.features.shikimori.logic.repo.ProfileItemRepository
import com.san.kir.features.shikimori.logic.repo.SettingsRepository
import com.san.kir.features.shikimori.logic.repo.TokenRepository
import com.san.kir.features.shikimori.logic.useCases.AuthUseCase
import io.ktor.client.HttpClient
import okhttp3.Cache
import java.io.File

private const val CACHE_SIZE = 5L * 1024 * 1024
private var singletonCache: Cache? = null
private var singletonClient: HttpClient? = null

private val ManualDI.cache: Cache
    get() = singletonCache ?: run {
        val cacheDir = File(context.cacheDir, "shiki")
        val instance = Cache(cacheDir, CACHE_SIZE)
        singletonCache = instance
        instance
    }

private val ManualDI.client: HttpClient
    get() = singletonClient ?: run {
        val instance = createKtorClient(cache, settingsRepository)
        singletonClient = instance
        instance
    }

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao, shikimoriDao)

internal val ManualDI.tokenRepository: TokenRepository
    get() = TokenRepository(client, settingsRepository)

internal val ManualDI.authUseCase: AuthUseCase
    get() = AuthUseCase(tokenRepository, settingsRepository)

internal val ManualDI.libraryItemRepository: LibraryItemRepository
    get() = LibraryItemRepository(shikimoriDao)

internal val ManualDI.profileItemRepository: ProfileItemRepository
    get() = ProfileItemRepository(shikimoriDao, client)

internal val ManualDI.chapterRepository: ChapterRepository
    get() = ChapterRepository(chapterDao)

internal val ManualDI.syncManager: SyncManager
    get() = SyncManager(profileItemRepository, chapterRepository)
