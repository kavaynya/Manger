package com.san.kir.data.db.catalog.mappers

import com.san.kir.data.db.catalog.custom.DbSimplifiedCatalogItem
import com.san.kir.data.models.catalog.MiniCatalogItem

internal fun DbSimplifiedCatalogItem.toModel(): MiniCatalogItem {
    return MiniCatalogItem(
        id, catalogName, name, statusEdition, shortLink, link, genres, type, authors, dateId,
        populate
    )
}

internal fun List<DbSimplifiedCatalogItem>.toModels(): List<MiniCatalogItem> {
    return map(DbSimplifiedCatalogItem::toModel)
}
