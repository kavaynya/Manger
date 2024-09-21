package com.san.kir.features.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface IdContainer {
    @SerialName("id")
    val id: Long?

    @Serializable
    data class Simple(@SerialName("id") override val id: Long?) : IdContainer
}
