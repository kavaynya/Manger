package com.san.kir.data.models.main

import com.san.kir.data.models.utils.SortLibraryUtil

public data class CategoryWithMangas(
    val id: Long = 0,
    val name: String = "",
    val typeSort: String = SortLibraryUtil.ABC,
    val isReverseSort: Boolean = false,
    val spanPortrait: Int = 2,
    val spanLandscape: Int = 3,
    val mangas: List<SimplifiedManga> = emptyList(),
) {
    override fun toString(): String = buildString {
        append("CategoryWithMangas(name=$name, mangas=[")
        mangas.forEach {
            append("(name=${it.name}, noRead=${it.noRead}), ")
        }
        append("])")
    }
}
