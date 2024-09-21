package com.san.kir.features.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Auth(
    @SerialName("token_container") val tokenContainer: TokenContainer = TokenContainer(),
    @SerialName("user") val user: User = User()
) {
    val isLogin: Boolean = tokenContainer.isNotEmpty()
    val hasUser: Boolean = user.id != 0L && user.nickname.isNotEmpty()
}
