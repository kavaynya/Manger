package com.san.kir.core.utils

import androidx.annotation.StringRes

public sealed interface Text {
    public data class Resource(@StringRes val id: Int) : Text
    public data class Simple(val text: String) : Text
}
