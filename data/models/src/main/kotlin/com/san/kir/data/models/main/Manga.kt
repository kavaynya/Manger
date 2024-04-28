package com.san.kir.data.models.main

import android.os.Parcelable
import com.san.kir.data.models.utils.ChapterFilter
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Manga(
    val id: Long = 0,
    val host: String = "",
    val name: String = "",
    val logo: String = "",
    val about: String = "",
    val categoryId: Long = 0,
    val path: String = "",
    val status: String = "",
    val color: Int = 0,
    val populate: Int = 0,
    val order: Int = 0,
    val isAlternativeSort: Boolean = true,
    val isUpdate: Boolean = true,
    val chapterFilter: ChapterFilter = ChapterFilter.ALL_READ_ASC,
    val isAlternativeSite: Boolean = false,
    val shortLink: String = "",
    val authorsList: List<String> = listOf(),
    val genresList: List<String> = listOf(),
    val lastUpdateError: String = ""
) : Parcelable

val Manga.authorsStr: String get() = authorsList.joinToString()
val Manga.genresStr: String get() = genresList.joinToString()
