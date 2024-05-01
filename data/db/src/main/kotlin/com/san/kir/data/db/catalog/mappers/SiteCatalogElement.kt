package com.san.kir.data.db.catalog.mappers

import com.san.kir.data.db.catalog.entities.DbSiteCatalogElement
import com.san.kir.data.models.catalog.SiteCatalogElement

internal fun DbSiteCatalogElement.toModel(): SiteCatalogElement {
    return SiteCatalogElement(
        id, host, catalogName, name, shortLink, link, type, authors, statusEdition, statusTranslate,
        volume, genres, about, populate, logo, dateId, isFull
    )
}

internal fun SiteCatalogElement.toEntity(): DbSiteCatalogElement {
    return DbSiteCatalogElement(
        id, host, catalogName, name, shortLink, link, type, authors, statusEdition, statusTranslate,
        volume, genres, about, populate, logo, dateId, isFull
    )
}

internal fun List<SiteCatalogElement>.toEntities(): List<DbSiteCatalogElement> {
    return map(SiteCatalogElement::toEntity)
}
