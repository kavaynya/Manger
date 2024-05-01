package com.san.kir.data.models.main

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.lengthMb
import kotlinx.parcelize.Parcelize
import java.io.File

@Stable
@Parcelize
data class Storage(
    var id: Long = 0,
    val name: String = "",
    val path: String = "",
    val sizeFull: Double = 0.0,
    val sizeRead: Double = 0.0,
    val catalogName: String = "",
) : Parcelable

fun Storage.getSizes(file: File, chapters: List<Chapter>?): Storage {
    return copy(
        sizeFull = file.lengthMb,
        sizeRead = chapters?.filter { it.isRead }?.sumOf { getFullPath(it.path).lengthMb } ?: 0.0
    )
}

