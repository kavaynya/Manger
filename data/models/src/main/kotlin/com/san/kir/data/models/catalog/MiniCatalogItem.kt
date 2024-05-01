package com.san.kir.data.models.catalog

data class MiniCatalogItem(
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
    sealed interface State {
        data object Added : State
        data object Update : State
        data object None : State
    }
}

fun MiniCatalogItem.toFullItem(): SiteCatalogElement {
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

