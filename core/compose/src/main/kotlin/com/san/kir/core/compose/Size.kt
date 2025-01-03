package com.san.kir.core.compose

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Size

public val Size.Companion.Saver: Saver<Size, *>
    get() = Saver(
        save = { it.width to it.height },
        restore = { Size(it.first, it.second) },
    )
