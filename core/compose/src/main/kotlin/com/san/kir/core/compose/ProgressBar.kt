package com.san.kir.core.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp

@Composable
public fun ToolbarProgress() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .size(Dimensions.ProgressBar.toolbar)
            .padding(end = Dimensions.quarter)
    )
}

@Composable
public fun TextProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(Dimensions.ProgressBar.default)
            .padding(start = Dimensions.quarter, top = Dimensions.smallest),
        strokeWidth = Dimensions.ProgressBar.strokeSmall,
    )
}

@Composable
public fun LinearProgress(modifier: Modifier = Modifier, height: Dp = Dimensions.half) {
    LinearProgressIndicator(
        modifier = modifier
            .size(height)
            .fillMaxWidth(),
        strokeCap = StrokeCap.Round
    )
}

@Composable
public fun LinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Dp = Dimensions.half
) {
    LinearProgressIndicator(
        progress,
        modifier = modifier
            .size(height)
            .fillMaxWidth(),
        strokeCap = StrokeCap.Round
    )
}
