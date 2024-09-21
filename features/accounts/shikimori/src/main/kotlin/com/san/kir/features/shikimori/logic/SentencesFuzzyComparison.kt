package com.san.kir.features.shikimori.logic

import com.san.kir.core.utils.fuzzy
import com.san.kir.features.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.shikimori.logic.models.LibraryMangaItem
import com.san.kir.features.shikimori.logic.models.MangaItem

internal infix fun MangaItem.fuzzy(that: MangaItem): Pair<Double, Boolean> {
    return when (this) {
        is AccountMangaItem -> this fuzzy (that as LibraryMangaItem)
        is LibraryMangaItem -> this fuzzy (that as AccountMangaItem)
        else -> 0.0 to false
    }
}

internal infix fun LibraryMangaItem.fuzzy(that: AccountMangaItem) = that fuzzy this
internal infix fun AccountMangaItem.fuzzy(that: LibraryMangaItem): Pair<Double, Boolean> {
    // сравниваем названия с помочью нечеткого сравнения
    val fuzzy1 = name fuzzy that.name
    val fuzzy2 = english fuzzy that.name

    // Если хотя бы одно из них дало положительный результат
    // то находим значение наилучшего совпадения
    return maxOf(fuzzy1.first, fuzzy2.first) to (fuzzy1.second || fuzzy2.second)
}


