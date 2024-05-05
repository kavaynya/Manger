package com.san.kir.core.compose

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
fun topInsets(
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
fun topInsetsPadding(top: Dp = Dimensions.zero) = topInsets(top = top).asPaddingValues()

@Composable
fun Modifier.topInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = windowInsetsPadding(topInsets(left, top, right, bottom))

@Composable
fun Modifier.topInsetsPadding(
    horizontal: Dp = Dimensions.zero,
    vertical: Dp = Dimensions.zero,
) = windowInsetsPadding(topInsets(horizontal, vertical, horizontal, vertical))


//////////////////////////////////////////


@Composable
fun bottomInsets(
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
fun bottomInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = bottomInsets(left, top, right, bottom).asPaddingValues()

@Composable
fun bottomInsetsPadding(all: Dp = Dimensions.zero) =
    bottomInsets(all, all, all, all).asPaddingValues()

@Composable
fun Modifier.bottomInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = windowInsetsPadding(bottomInsets(left, top, right, bottom))


///////////////////////////////////////


@Composable
fun horizontalInsets(
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
fun horizontalInsetsPadding(all: Dp = Dimensions.zero) =
    horizontalInsets(all, all, all, all).asPaddingValues()

@Composable
fun Modifier.horizontalInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = windowInsetsPadding(horizontalInsets(left, top, right, bottom))

@Composable
fun Modifier.horizontalInsets(
    horizontal: Dp = Dimensions.zero,
    vertical: Dp = Dimensions.zero,
) = windowInsetsPadding(horizontalInsets(horizontal, vertical, horizontal, vertical))


/////////////////////////////////////


@Composable
fun startInsets(
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
fun Modifier.startInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = windowInsetsPadding(startInsets(left, top, right, bottom))


////////////////////////////////////


@Composable
fun endInsets(
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
fun Modifier.endInsetsPadding(
    left: Dp = Dimensions.zero,
    top: Dp = Dimensions.zero,
    right: Dp = Dimensions.zero,
    bottom: Dp = Dimensions.zero
) = windowInsetsPadding(endInsets(left, top, right, bottom))


//////////////////////////////////


@Composable
fun horizontalAndBottomInsetsPadding() =
    horizontalInsets().add(bottomInsets()).asPaddingValues()
