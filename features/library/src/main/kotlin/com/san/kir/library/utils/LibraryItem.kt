package com.san.kir.library.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.endInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.rememberImage
import com.san.kir.core.compose.squareMaxSize
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.TestTags
import com.san.kir.core.utils.categoryAll
import com.san.kir.data.models.main.SimplifiedManga

private val CornerRadius = Dimensions.half
private val BetweenItemPadding = Dimensions.quarter

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyGridItemScope.LibraryLargeItem(
    onClick: (Long, SharedParams) -> Unit,
    onLongClick: (SimplifiedManga) -> Unit,
    manga: SimplifiedManga,
    cat: String,
    showCategory: Boolean,
) {
    val defaultColor = MaterialTheme.colorScheme.primary
    val backgroundColor by remember { mutableStateOf(manga.composeColor(defaultColor)) }
    val textColor = contentColorFor(backgroundColor)
    val buttonParams = rememberSharedParams(cornerRadius = Dimensions.half)

    Card(
        shape = RoundedCornerShape(CornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .animateItem()
            .testTag(TestTags.Library.item)
            .padding(BetweenItemPadding)
            .fillMaxWidth()
            .saveParams(buttonParams)
            .combinedClickable(
                onLongClick = { onLongClick(manga) },
                onClick = { onClick(manga.id, buttonParams) },
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.squareMaxSize()) {
                    LogoImage(
                        manga.logo,
                        Modifier
                            .padding(Dimensions.smallest)
                            .fillMaxSize()
                    )
                    if (manga.hasError) {
                        Icon(
                            painterResource(com.san.kir.core.compose.R.drawable.unknown),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            tint = Color.Unspecified
                        )
                    }
                }

                Text(
                    text = manga.name,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                        .padding(horizontal = 6.dp),
                    color = textColor,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                if (cat == ManualDI.categoryAll() && showCategory)
                    CategoryName(
                        manga.category,
                        defaultColor,
                        modifier = Modifier
                            .padding(end = Dimensions.quarter)
                            .align(Alignment.CenterVertically)
                    )

                Text(
                    text = "${manga.noRead}",
                    maxLines = 1,
                    modifier = Modifier
                        .background(backgroundColor, RoundedCornerShape(bottomStart = CornerRadius))
                        .padding(Dimensions.quarter),
                    color = textColor
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyItemScope.LibrarySmallItem(
    onClick: (Long, SharedParams) -> Unit,
    onLongClick: (SimplifiedManga) -> Unit,
    manga: SimplifiedManga,
    cat: String,
    showCategory: Boolean,
) {
    val defaultColor = MaterialTheme.colorScheme.primary
    val backgroundColor by remember { mutableStateOf(manga.composeColor()) }
    val buttonParams = rememberSharedParams(cornerRadius = Dimensions.half)

    Card(
        shape = RoundedCornerShape(CornerRadius),
        border = BorderStroke(Dimensions.quarter, backgroundColor),
        modifier = Modifier
            .animateItem()
            .testTag(TestTags.Library.item)
            .padding(BetweenItemPadding)
            .fillMaxWidth()
            .saveParams(buttonParams)
            .combinedClickable(
                onLongClick = { onLongClick(manga) },
                onClick = { onClick(manga.id, buttonParams) },
            )
    ) {
        Box(
            modifier = Modifier
                .endInsetsPadding()
                .padding(Dimensions.smallest)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalInsetsPadding()
                    .padding(Dimensions.quarter)
            ) {
                LogoImage(
                    manga.logo,
                    modifier = Modifier
                        .padding(Dimensions.quarter)
                        .size(Dimensions.Image.bigger),
                )

                Text(
                    text = manga.name,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f, true)
                        .padding(start = Dimensions.half)
                        .align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${manga.noRead}",
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = Dimensions.half)
                        .align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (manga.hasError) {
                    Icon(
                        painterResource(com.san.kir.core.compose.R.drawable.unknown),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }

                if (cat == ManualDI.categoryAll() && showCategory)
                    CategoryName(
                        manga.category,
                        defaultColor,
                        modifier = Modifier.padding(Dimensions.smallest),
                    )
            }
        }
    }
}

@Composable
internal fun LogoImage(logo: String, modifier: Modifier) {
    Image(
        painter = rememberImage(logo),
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(CornerRadius)),
        contentScale = ContentScale.Crop
    )
}

@Composable
internal fun CategoryName(
    category: String,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = category,
        modifier = modifier
            .background(color = contentColorFor(contentColor), shape = RoundedCornerShape(50))
            .padding(horizontal = Dimensions.half, vertical = Dimensions.quarter),
        color = contentColor,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun contentColorFor(background: Color): State<Color> = remember {
    derivedStateOf { if (background.luminance() > 0.5f) Color.Black else Color.White }
}

