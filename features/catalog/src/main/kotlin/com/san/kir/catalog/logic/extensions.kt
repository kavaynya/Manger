package com.san.kir.catalog.logic

import com.san.kir.data.models.main.Manga
import com.san.kir.data.parsing.SiteCatalog

internal fun List<Manga>.linksForCatalog(siteCatalog: SiteCatalog): List<String> {
    return filter { manga ->
        siteCatalog.allCatalogName.any { manga.host.contains(it) }
    }
        .map { it.shortLink }
}
