package com.san.kir.core.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
public fun topInsets(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): WindowInsets {
    return WindowInsets.displayCutout
        .only(WindowInsetsSides.Top)
        .union(WindowInsets.systemBars.only(WindowInsetsSides.Top))
        .add(WindowInsets(left, top, right, bottom))
}

@Composable
public fun topInsetsPadding(top: Dp = Dimensions.zero): PaddingValues =
    topInsets(top = top).asPaddingValues()

@Composable
public fun Modifier.topInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): Modifier = windowInsetsPadding(topInsets(left, top, right, bottom))

@Composable
public fun Modifier.topInsetsPadding(
    horizontal: Dp = Dimensions.zero,
    vertical: Dp = Dimensions.zero,
): Modifier = windowInsetsPadding(topInsets(horizontal, vertical, horizontal, vertical))


//////////////////////////////////////////


@Composable
public fun bottomInsets(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): WindowInsets {
    return WindowInsets.displayCutout
        .only(WindowInsetsSides.Bottom)
        .union(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
        .add(WindowInsets(left, top, right, bottom))
}

@Composable
public fun bottomInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): PaddingValues = bottomInsets(left, top, right, bottom).asPaddingValues()

@Composable
public fun bottomInsetsPadding(all: Dp = Dimensions.zero): PaddingValues =
    bottomInsets(all, all, all, all).asPaddingValues()

@Composable
public fun Modifier.bottomInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): Modifier = windowInsetsPadding(bottomInsets(left, top, right, bottom))

@Composable
public fun Modifier.bottomInsetsPadding(
    all: Dp = Dimensions.zero,
): Modifier = windowInsetsPadding(bottomInsets(all, all, all, all))


///////////////////////////////////////


@Composable
public fun horizontalInsets(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): WindowInsets {
    return WindowInsets.displayCutout
        .only(WindowInsetsSides.Horizontal)
        .union(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
        .add(WindowInsets(left, top, right, bottom))
}

@Composable
public fun horizontalInsetsPadding(all: Dp = Dimensions.zero): PaddingValues =
    horizontalInsets(all, all, all, all).asPaddingValues()

@Composable
public fun Modifier.horizontalInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): Modifier = windowInsetsPadding(horizontalInsets(left, top, right, bottom))

@Composable
public fun Modifier.horizontalInsetsPadding(
    horizontal: Dp = Dimensions.zero,
    vertical: Dp = Dimensions.zero,
): Modifier = windowInsetsPadding(horizontalInsets(horizontal, vertical, horizontal, vertical))

@Composable
public fun Modifier.horizontalInsetsPadding(
    all: Dp = Dimensions.zero,
): Modifier = windowInsetsPadding(horizontalInsets(all, all, all, all))

/////////////////////////////////////


@Composable
public fun startInsets(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): WindowInsets {
    return WindowInsets.displayCutout
        .only(WindowInsetsSides.Start)
        .union(WindowInsets.systemBars.only(WindowInsetsSides.Start))
        .add(WindowInsets(left, top, right, bottom))
}

@Composable
public fun Modifier.startInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): Modifier = windowInsetsPadding(startInsets(left, top, right, bottom))

@Composable
public fun Modifier.startInsetsPadding(
    horizontal: Dp = Dimensions.zero,
    vertical: Dp = Dimensions.zero,
): Modifier = windowInsetsPadding(startInsets(horizontal, vertical, horizontal, vertical))


////////////////////////////////////


@Composable
public fun endInsets(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): WindowInsets {
    return WindowInsets.displayCutout
        .only(WindowInsetsSides.End)
        .union(WindowInsets.systemBars.only(WindowInsetsSides.End))
        .add(WindowInsets(left, top, right, bottom))
}

@Composable
public fun Modifier.endInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
): Modifier = windowInsetsPadding(endInsets(left, top, right, bottom))


//////////////////////////////////


@Composable
public fun horizontalAndBottomInsetsPadding(): PaddingValues =
    horizontalInsets().add(bottomInsets()).asPaddingValues()
