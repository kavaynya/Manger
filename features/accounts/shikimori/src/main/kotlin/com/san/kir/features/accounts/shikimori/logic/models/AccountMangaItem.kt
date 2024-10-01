package com.san.kir.features.accounts.shikimori.logic.models

import android.os.Parcelable
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.models.main.AccountManga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
public data class AccountMangaItem(
    override val id: Long = -1L,
    val accountId: Long = -1L,
    override val name: String = "",
    val english: String = "",
    val russian: String = "",
    override val logo: String = "",
    override val read: Int = 0,
    override val all: Int = 0,
    val volumes: Int = 0,
    override val description: String = "",
    val idInLibrary: Long = -1L,
    val idInAccount: Long = -1L,
    val idInSite: Long = -1L,
    val mangaScore: Float = 0.0f,
    val userScore: Int = 0,
    val rewatches: Int = 0,
    override val status: ShikimoriStatus = ShikimoriStatus.Watching,
    val genres: List<String> = emptyList()
) : MangaItem, Parcelable {
    @IgnoredOnParcel val inLibrary: Boolean = idInLibrary != -1L
    @IgnoredOnParcel val inAccount: Boolean = idInAccount != -1L
}

internal fun AccountManga.toMangaItem(): AccountMangaItem {
    val data = ManualDI.stringToJson<ShikimoriRate>(data)
    val manga = data?.manga
    return AccountMangaItem(
        id = id,
        accountId = accountId,
        name = manga?.preparedName ?: "",
        english = manga?.english ?: "",
        russian = manga?.russian ?: "",
        logo = manga?.logo ?: "",
        read = data?.chapters ?: 0,
        all = manga?.chapters ?: 0,
        volumes = manga?.volumes ?: 0,
        description = manga?.description ?: "",
        idInLibrary = libraryId,
        idInAccount = targetId,
        idInSite = mangaId,
        mangaScore = manga?.score ?: 0.0f,
        userScore = data?.score ?: 0,
        rewatches = data?.rewatches ?: 0,
        status = data?.status ?: ShikimoriStatus.Watching,
        genres = manga?.genres?.mapNotNull { it.russian } ?: emptyList()
    )
}

internal fun List<AccountManga>.toMangaItems(): List<AccountMangaItem> {
    return map(AccountManga::toMangaItem)
}

@JvmName("toFlowAccountMangaItem")
internal fun Flow<List<AccountManga>>.toMangaItems(): Flow<List<AccountMangaItem>> {
    return map(List<AccountManga>::toMangaItems)
}

internal fun AccountMangaItem.update(rate: ShikimoriRate): AccountMangaItem {
    val manga = rate.manga
    return copy(
        name = manga?.name ?: name,
        english = manga?.english ?: english,
        russian = manga?.russian ?: russian,
        logo = manga?.image?.original ?: logo,
        read = rate.chapters ?: read,
        all = manga?.chapters ?: all,
        volumes = manga?.volumes ?: volumes,
        description = manga?.description ?: description,
        mangaScore = manga?.score ?: mangaScore,
        userScore = rate.score ?: userScore,
        rewatches = rate.rewatches ?: rewatches,
        status = rate.status,
        genres = manga?.genres?.mapNotNull { it.russian } ?: genres
    )
}

internal fun AccountMangaItem.toRate(): ShikimoriRate {
    return ShikimoriRate(
        id = idInAccount,
        score = userScore,
        status = status,
        chapters = read,
        rewatches = rewatches,
        manga = ShikimoriManga(
            id = idInSite,
            name = name,
            russian = russian,
            english = english,
            score = mangaScore,
            volumes = volumes,
            chapters = all,
            image = ShikimoriManga.Poster(logo),
            genres = genres.map { ShikimoriManga.Genre(it, it) },
            description = description
        )
    )
}

internal fun AccountMangaItem.toModel(): AccountManga {
    return AccountManga(
        id = id,
        accountId = accountId,
        targetId = idInAccount,
        libraryId = idInLibrary,
        mangaId = idInSite,
        data = data()
    )
}

internal fun AccountMangaItem.data(): String {
    return ManualDI.jsonToString(toRate())
}

