package com.san.kir.catalog.logic.di

import com.san.kir.catalog.logic.repo.CatalogRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.catalogDatabaseFactory
import com.san.kir.data.categoryDao
import com.san.kir.data.mangaDao
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.statisticDao

internal val ManualDI.catalogRepository: CatalogRepository
    get() = CatalogRepository(
        siteCatalogsManager, catalogDatabaseFactory, mangaDao, categoryDao, statisticDao
    )
