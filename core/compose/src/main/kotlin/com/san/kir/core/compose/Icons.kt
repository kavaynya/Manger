package com.san.kir.core.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.san.kir.core.compose.animation.StartAnimatedVisibility

@Composable
public fun CloseIcon(show: Boolean = true, onClick: () -> Unit) {
    StartAnimatedVisibility(show) {
        IconButton(onClick = onClick) { Icon(Icons.Default.Close, "") }
    }
}
