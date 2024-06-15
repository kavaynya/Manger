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


private val StorageShape: RoundedCornerShape = RoundedCornerShape(Dimensions.smaller)

private val storageTrackColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

private val storageUsedColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

private val storageReadColor: Color
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
            .background(color = storageTrackColor, shape = StorageShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fullPercent)
                .background(storageUsedColor, shape = StorageShape)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(readPercent)
                .background(storageReadColor, shape = StorageShape)
        )
    }
}
