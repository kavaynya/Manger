package com.san.kir.storage.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.san.kir.core.compose.Dimensions


internal val StorageShape: RoundedCornerShape = RoundedCornerShape(Dimensions.smaller)

internal val StorageTrackColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

internal val StorageUsedColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

internal val StorageReadColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.tertiary

@Composable
internal fun StorageProgressBar(
    max: Double,
    full: Double,
    read: Double,
    modifier: Modifier = Modifier,
) {

    val fullPercent = if (max == 0.0) 0F else (full / max).toFloat()
    val readPercent = if (max == 0.0) 0F else (read / max).toFloat()

    Box(
        modifier = modifier
            .background(color = StorageTrackColor, shape = StorageShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fullPercent)
                .background(StorageUsedColor, shape = StorageShape)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(readPercent)
                .background(StorageReadColor, shape = StorageShape)
        )
    }
}
