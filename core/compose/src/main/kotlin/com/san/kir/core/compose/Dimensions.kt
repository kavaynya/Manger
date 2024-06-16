package com.san.kir.core.compose

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

public object Dimensions {
    public val zero: Dp = 0.dp
    public val smallest: Dp = 2.dp
    public val quarter: Dp = 4.dp
    public val smaller: Dp = 6.dp
    public val half: Dp = 8.dp
    public val middle: Dp = 12.dp
    public val default: Dp = 16.dp
    public val bigger: Dp = 20.dp
    public val big: Dp = 24.dp

    public object Image {
        public val mini: Dp = 26.dp
        public val small: Dp = 36.dp
        public val default: Dp = 56.dp
        public val bigger: Dp = 64.dp
        public val big: Dp = 126.dp

        public val storage: DpSize = DpSize(52.dp, 30.dp)
    }

    public object ProgressBar {
        public val default: Dp = 18.dp
        public val big: Dp = 30.dp
        public val toolbar: Dp = 40.dp

        public val storage: Dp = 60.dp

        public val strokeSmall: Dp = 2.dp
        public val strokeDefault: Dp = 4.dp
    }

    public object Items {
        public val height: Dp = 48.dp
    }

    public val appBarHeight: Dp = 56.dp
}
