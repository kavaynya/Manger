package com.san.kir.library.utils

import androidx.compose.ui.graphics.Color
import com.san.kir.data.models.main.SimplifiedManga

internal fun SimplifiedManga.composeColor(defaultColor: Color? = null): Color {
    val defaultColor = defaultColor ?: Color.Unspecified
    if (color == 0) return defaultColor
    return runCatching { Color(color) }.getOrDefault(defaultColor)
}
