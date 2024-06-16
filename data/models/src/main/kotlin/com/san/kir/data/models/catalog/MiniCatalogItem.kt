package com.san.kir.data.models.catalog

public data class MiniCatalogItem(
    val id: Long = 0,
    val catalogName: String = "",
    val name: String = "",
    val statusEdition: String = "",
    val shortLink: String = "",
    val link: String = "",
    val genres: List<String> = emptyList(),
    val type: String = "",
    val authors: List<String> = emptyList(),
    val dateId: Int = 0,
    val populate: Int = 0,
    val state: State = State.None
) {
    public sealed interface State {
        public data object Added : State
        public data object Update : State
        public data object None : State
    }
}

public fun MiniCatalogItem.toFullItem(): SiteCatalogElement {
    return SiteCatalogElement(
        id = id,
        catalogName = catalogName,
        name = name,
        statusEdition = statusEdition,
        shortLink = shortLink,
        link = link,
        genres = genres,
        type = type,
        authors = authors,
        dateId = dateId,
        populate = populate
    )
}

