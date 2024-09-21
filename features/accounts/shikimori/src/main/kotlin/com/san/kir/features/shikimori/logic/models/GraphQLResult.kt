package com.san.kir.features.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GraphQLResult(
    @SerialName("data") val data: Data
) {

    @Serializable
    internal data class Data(
        @SerialName("currentUser") val whoami: User? = null,
        @SerialName("userRates") val userRates: List<ShikimoriRate> = emptyList(),
        @SerialName("mangas") val manga: List<ShikimoriManga> = emptyList()
    )
}
