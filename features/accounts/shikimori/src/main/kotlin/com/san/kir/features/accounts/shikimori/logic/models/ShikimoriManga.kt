package com.san.kir.features.accounts.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ShikimoriManga(
    @SerialName("id") val id: Long = -1L,
    @SerialName("name") val name: String? = null,
    @SerialName("russian") val russian: String? = null,
    @SerialName("english") val english: String? = null,
    @SerialName("score") val score: Float? = null,
    @SerialName("volumes") val volumes: Int? = null,
    @SerialName("chapters") val chapters: Int? = null,
    @SerialName("poster") val image: Poster? = null,
    @SerialName("genres") val genres: List<Genre>? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("userRate") val rate: ShikimoriRate? = null
) {
    fun isEmpty(): Boolean = id == -1L

    val preparedName = russian?.takeIf { it.isNotEmpty() } ?: name ?: english ?: ""
    val logo = image?.original ?: ""

    @Serializable
    data class Poster(
        @SerialName("originalUrl") val original: String? = null
    )

    @Serializable
    data class Genre(
        @SerialName("name") val name: String? = null,
        @SerialName("russian") val russian: String? = null
    )
}


internal fun ShikimoriManga.toMangaItem(accountId: Long): AccountMangaItem {
    return AccountMangaItem(
        id = 0L,
        accountId = accountId,
        name = preparedName,
        english = english ?: "",
        russian = russian ?: "",
        logo = logo,
        read = rate?.chapters ?: 0,
        all = chapters ?: 0,
        volumes = volumes ?: 0,
        description = description ?: "",
        idInLibrary = -1L,
        idInAccount = rate?.id ?: -1L,
        idInSite = id,
        mangaScore = score ?: 0f,
        userScore = rate?.score ?: 0,
        rewatches = rate?.rewatches ?: 0,
        status = rate?.status ?: ShikimoriStatus.Watching,
        genres = genres?.mapNotNull { it.russian } ?: emptyList()
    )
}

internal fun List<ShikimoriManga>.toMangaItems(accountId: Long): List<AccountMangaItem> {
    return map { it.toMangaItem(accountId) }
}
