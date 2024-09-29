package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.Fonts
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.rememberImage
import com.san.kir.features.accounts.shikimori.R
import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriStatus
import com.san.kir.features.accounts.shikimori.logic.useCases.CanBind

@Composable
internal fun MangaItemContent(
    avatar: String,
    mangaName: String,
    canBind: CanBind,
    modifier: Modifier = Modifier,
    readingChapters: Int = 0,
    allChapters: Int = 0,
    secondaryText: String? = null,
    currentStatus: ShikimoriStatus? = null,
    inAccount: Boolean? = null,
    onClick: (SharedParams) -> Unit,
) {
    val params = rememberSharedParams()
    Row(
        modifier = Modifier
            .saveParams(params)
            .clickable(onClick = { onClick(params) })
            .fillMaxWidth()
            .padding(vertical = Dimensions.half, horizontal = Dimensions.default)
            .horizontalInsetsPadding()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MangaLogo(avatar)

        DefaultSpacer()

        Column(modifier = Modifier.weight(1f)) {
            MangaName(mangaName)
            Text(
                secondaryText ?: stringResource(R.string.reading, readingChapters, allChapters),
                fontSize = Fonts.Size.less,
            )
            currentStatus?.let { StatusText(it) }
        }

        DefaultSpacer()

        if (canBind == CanBind.No) {
            if (inAccount == true) {
                Icon(Icons.Default.PersonAddAlt1, "has synchronized item")
            }
        } else {
            Box(
                modifier = Modifier.size(Dimensions.Image.small),
                contentAlignment = Alignment.Center
            ) {
                when (canBind) {
                    CanBind.Already -> Icon(
                        Icons.Default.SyncAlt,
                        contentDescription = "has synchronized item"
                    )

                    CanBind.Ok -> Icon(
                        Icons.Default.NotificationImportant,
                        contentDescription = "has synchronized item"
                    )

                    CanBind.Check -> Icon(
                        Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "has synchronized item"
                    )

                    else -> Unit
                }
            }
        }
    }
}

@Composable
internal fun MangaName(name: String, modifier: Modifier = Modifier) {
    Text(
        name,
        modifier,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis,
        softWrap = false,
        minLines = 1,
    )
}

@Composable
internal fun MangaLogo(url: String) {
    Image(
        rememberImage(url),
        "manga avatar",
        modifier = Modifier.clip(RoundedCornerShape(Dimensions.half))
            .size(Dimensions.Image.default)
            .background(Color.Gray),
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
internal fun ListItemContentPreview() {
    LazyColumn {
        item {
            MangaItemContent(
                avatar = "",
                mangaName = "item.manga.russian",
                readingChapters = 10,
                allChapters = 99,
                canBind = CanBind.Already,
                currentStatus = ShikimoriStatus.Planned
            ) {}
        }

        item {
            MangaItemContent(
                avatar = "",
                mangaName = "item.manga.russian",
                readingChapters = 10,
                allChapters = 99,
                canBind = CanBind.Ok,
            ) {}
        }
    }
}
