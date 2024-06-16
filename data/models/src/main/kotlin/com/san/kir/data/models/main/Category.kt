package com.san.kir.data.models.main

import android.os.Parcelable
import com.san.kir.data.models.utils.SortLibraryUtil
import kotlinx.parcelize.Parcelize

@Parcelize
public data class Category(
    val id: Long = 0L,
    val name: String = "",
    val order: Int = 0,
    val isVisible: Boolean = true,
    val typeSort: String = SortLibraryUtil.ABC,
    val isReverseSort: Boolean = false,
    val spanPortrait: Int = 2,
    val spanLandscape: Int = 3,
) : Parcelable
