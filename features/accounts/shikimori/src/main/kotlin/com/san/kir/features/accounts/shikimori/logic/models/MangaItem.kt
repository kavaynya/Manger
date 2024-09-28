package com.san.kir.features.accounts.shikimori.logic.models

internal interface MangaItem {
    val all: Int
    val description: String
    val id: Long
    val logo: String
    val name: String
    val read: Int
    val status: ShikimoriStatus?
        get() = null
}
