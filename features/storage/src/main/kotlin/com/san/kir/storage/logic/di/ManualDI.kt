package com.san.kir.storage.logic.di

import com.san.kir.core.utils.ManualDI
import com.san.kir.data.mangaDao
import com.san.kir.data.storageDao
import com.san.kir.storage.logic.repo.StorageRepository

internal val ManualDI.storageRepository: StorageRepository
    get() = StorageRepository(storageDao, mangaDao)
