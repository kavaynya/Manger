package com.san.kir.library.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.categoryDao
import com.san.kir.data.chapterDao
import com.san.kir.data.mainMenuDao
import com.san.kir.data.mangaDao
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.plannedDao
import com.san.kir.data.settingsDao
import com.san.kir.data.storageDao
import com.san.kir.library.logic.repo.MainMenuRepository
import com.san.kir.library.logic.repo.MangaRepository
import com.san.kir.library.logic.repo.SettingsRepository

internal val ManualDI.mangaRepository: MangaRepository
    get() = MangaRepository(context, mangaDao, categoryDao)

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)

internal val ManualDI.mainMenuRepository: MainMenuRepository
    get() = MainMenuRepository(
        context,
        mainMenuDao,
        mangaDao,
        storageDao,
        categoryDao,
        chapterDao,
        plannedDao,
        siteCatalogsManager
    )
