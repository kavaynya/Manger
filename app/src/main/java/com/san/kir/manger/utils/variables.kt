package com.san.kir.manger.utils

import com.san.kir.manger.App
import com.san.kir.manger.R
import java.text.SimpleDateFormat
import java.util.*

// Названия папок
object DIR {
    const val ROOT = "Manger"
    const val PROFILE = "$ROOT/profile"
    const val CATALOGS = "$ROOT/catalogs"
    const val MANGA = "$ROOT/manga"
    const val CACHE = "$PROFILE/.cache"
    private const val LOCAL = "$MANGA/local"
    val ALL = listOf(CATALOGS, MANGA, PROFILE, LOCAL, CACHE)
}


// Настройки
val CATEGORY_ALL: String = App.context.getString(R.string.category_all)

const val sPrefListChapters = "ListChaptersActivity"


// Логи
const val TAG = "myLogs"

val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))

enum class SortLibrary {
    AddTime,
    AbcSort,
    Populate
}
