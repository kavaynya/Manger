package com.san.kir.features.shikimori.logic.models

internal object EmptyMangaItem : MangaItem {
    override val all = 0
    override val read = 0
    override val id: Long = -1
    override val name = ""
    override val logo = ""
    override val description = ""
}
