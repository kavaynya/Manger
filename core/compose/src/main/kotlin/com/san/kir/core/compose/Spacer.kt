package com.san.kir.core.compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public fun SmallestSpacer() {
    Spacer(modifier = Modifier.height(Dimensions.smallest))
}

@Composable
public fun QuarterSpacer() {
    Spacer(modifier = Modifier.width(Dimensions.quarter))
}

@Composable
public fun HalfSpacer() {
    Spacer(modifier = Modifier.size(Dimensions.half))
}

@Composable
public fun DefaultSpacer() {
    Spacer(modifier = Modifier.size(Dimensions.default))
}

@Composable
public fun BigSpacer() {
    Spacer(modifier = Modifier.height(Dimensions.big))
}

@Composable
public fun RowScope.FullWeightSpacer() {
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
public fun ColumnScope.FullWeightSpacer() {
    Spacer(modifier = Modifier.weight(1f))
}

