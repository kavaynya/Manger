package com.san.kir.core.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

public fun findInGoogle(name: String): String =
    "https://www.google.com/s2/favicons?domain=$name"

public fun Text.text(context: Context): CharSequence {
    return when (this) {
        is Text.Resource -> context.getString(id)
        is Text.Simple -> text
    }
}

@Composable
public fun Text.text(): CharSequence {
    val context = LocalContext.current
    return text(context)
}
