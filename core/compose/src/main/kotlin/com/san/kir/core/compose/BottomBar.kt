package com.san.kir.core.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BarPadding = Dimensions.default
private val DefaultRoundedShape = RoundedCornerShape(50)

public val barContainerColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

@Composable
public fun DefaultBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .bottomInsetsPadding(BarPadding)
            .background(barContainerColor, DefaultRoundedShape)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
