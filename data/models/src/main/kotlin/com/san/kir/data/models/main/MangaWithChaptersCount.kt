package com.san.kir.data.models.main

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class MangaWithChaptersCount(
    val id: Long = 0,
    val name: String = "",
    val logo: String = "",
    val description: String = "",
    val sort: Boolean = false,
    val read: Long = 0,
    val all: Long = 0,
) : Parcelable
