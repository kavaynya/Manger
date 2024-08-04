package com.san.kir.features.viewer.utils

import android.os.Parcelable
import com.san.kir.data.models.main.Chapter
import kotlinx.parcelize.Parcelize

internal sealed class Page {
    data object NonePrev : Page()
    data object Prev : Page()

    @Parcelize
    data class Current(val pagelink: String, val chapter: Chapter = Chapter()) : Page(), Parcelable
    object Next : Page()
    object NoneNext : Page()
}
