package com.san.kir.data.models.catalog

import android.os.Parcelable
import com.san.kir.data.models.main.Manga
import kotlinx.parcelize.Parcelize

@Parcelize
data class SiteCatalogElement(
    val id: Long = 0,
    val host: String = "",
    val catalogName: String = "",
    val name: String = "",
    val shortLink: String = "",
    val link: String = "",
    val type: String = "",
    val authors: List<String> = emptyList(),
    val statusEdition: String = "",
    val statusTranslate: String = "",
    val volume: Int = 0,
    val genres: List<String> = emptyList(),
    val about: String = "",
    val populate: Int = 0,
    val logo: String = "",
    val dateId: Int = 0,
    val isFull: Boolean = false,
) : Parcelable

fun SiteCatalogElement.toManga(categoryId: Long, path: String): Manga {
    return Manga(
        name = name,
        host = host,
        authorsList = authors,
        logo = logo,
        about = about,
        categoryId = categoryId,
        genresList = genres,
        path = path,
        shortLink = shortLink,
        status = statusEdition
    )
}

fun String.toLinkOfFullElement(): SiteCatalogElement {
    return SiteCatalogElement(link = this)
}
