package com.san.kir.features.accounts.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenContainer(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("token_type") val tokenType: String = "",
    @SerialName("expires_in") val expiresIn: Long = 0L,
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("scope") val scope: String = "",
    @SerialName("created_at") val createdAt: Long = 0L
) {
    fun isNotEmpty(): Boolean = accessToken.isNotEmpty() && refreshToken.isNotEmpty()
    fun isEmpty(): Boolean = accessToken.isEmpty() || refreshToken.isEmpty()
}
