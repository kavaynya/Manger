package com.san.kir.chapters.logic.di

import com.san.kir.chapters.logic.repo.ChaptersRepository
import com.san.kir.chapters.logic.repo.LatestRepository
import com.san.kir.chapters.logic.repo.SettingsRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.chapterDao
import com.san.kir.data.mangaDao
import com.san.kir.data.settingsDao

internal val ManualDI.latestRepository: LatestRepository
    get() = LatestRepository(chapterDao)

internal val ManualDI.chaptersRepository: ChaptersRepository
    get() = ChaptersRepository(mangaDao, chapterDao)

internal val ManualDI.settingsRepository: SettingsRepository
    get() = SettingsRepository(settingsDao)
