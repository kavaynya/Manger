package com.san.kir.library.utils

import androidx.compose.ui.graphics.Color
import com.san.kir.core.compose.intToComposeColor
import com.san.kir.data.models.main.SimplifiedManga

internal fun SimplifiedManga.composeColor(defaultColor: Color? = null): Color = intToComposeColor(color, defaultColor)
