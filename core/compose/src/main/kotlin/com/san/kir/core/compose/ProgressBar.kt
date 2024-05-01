package com.san.kir.core.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToolbarProgress() {
    CircularProgressIndicator(
        color = contentColorFor(MaterialTheme.colorScheme.primarySurface),
        modifier = Modifier.size(Dimensions.ProgressBar.toolbar)
            .padding(end = Dimensions.quarter)
    )
}

@Composable
fun TextProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(Dimensions.ProgressBar.default)
            .padding(start = Dimensions.quarter, top = Dimensions.smallest),
        strokeWidth = Dimensions.ProgressBar.strokeSmall,
    )
}
