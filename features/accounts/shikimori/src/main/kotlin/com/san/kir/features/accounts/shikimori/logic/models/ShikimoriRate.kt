package com.san.kir.features.accounts.shikimori.logic.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ShikimoriRate(
    @SerialName("id") override val id: Long? = -1,
    @SerialName("score") val score: Int? = -1,
    @SerialName("status") val status: ShikimoriStatus = ShikimoriStatus.Planned,
    @SerialName("chapters") val chapters: Int? = 0,
    @SerialName("rewatches") val rewatches: Int? = 0,
    @SerialName("manga") val manga: ShikimoriManga? = ShikimoriManga()
) : IdContainer {

    override fun toString(): String {
        val genres = manga?.genres?.joinToString(prefix = "[", postfix = "]") {
            "${it.name}:${it.russian}"
        } ?: ""
        val desc = manga?.description?.take(50)?.let {
            if (it.length == 50) "$it..." else it
        } ?: ""
        return "ShikimoriRate(id: $id, status: $status, chapters: $chapters, rewatches: $rewatches, " +
                "mId: ${manga?.id}, mName: ${manga?.name}, mRussian: ${manga?.russian}, " +
                "mEnglish: ${manga?.english}, mScore: ${manga?.score}, mVolumes: ${manga?.volumes}, " +
                "mChapters: ${manga?.chapters}, mPoster: ${manga?.image}, mGenres: $genres, mDesc: $desc)"
    }
}
