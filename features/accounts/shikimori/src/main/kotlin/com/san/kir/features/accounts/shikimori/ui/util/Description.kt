package com.san.kir.features.accounts.shikimori.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.TextWithFirstWordBold
import com.san.kir.features.accounts.shikimori.R


// Отображение описания при его наличии и сворачиваемость при наличии в профиле
@Composable
internal fun Description(inAccount: Boolean, description: String?) {
    if (description.isNullOrEmpty()) return

    val smallText = 1
    val fullText = 100

    // Отображение свернутого текста зависит от нажатия на соответствующую кнопку
    // и текущего наличия в профиле пользователя
    var showFullDesc by remember { mutableStateOf(false) }
    val animateSize by animateIntAsState(
        // Пока происходит проверка текст развернут
        // Если уже добавлен в профиль, то свернуть текст и управлять кнопкой
        // Иначе не сворачивать
        if (inAccount) if (showFullDesc) fullText else smallText else fullText, label = ""
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        TextWithFirstWordBold(
            stringResource(R.string.profile_item_desc, description),
            textAlign = TextAlign.Justify,
            maxLines = animateSize,
        )

        // Отображение кнопки только, если манга у вас в профиле
        AnimatedVisibility(visible = inAccount) {
            TextButton(
                onClick = { showFullDesc = !showFullDesc },
                contentPadding = PaddingValues(vertical = Dimensions.zero),
            ) {
                if (showFullDesc)
                    Text(stringResource(R.string.desc_hide))
                else
                    Text(stringResource(R.string.desc_show))
            }
        }
    }
}

@Composable
internal fun Description(description: String?) {
    if (description.isNullOrEmpty()) return

    val smallText = 7
    val fullText = 100

    var showFullDesc by remember { mutableStateOf(false) }
    val animateSize by animateIntAsState(if (showFullDesc) fullText else smallText, label = "")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        TextWithFirstWordBold(
            stringResource(R.string.profile_item_desc, description),
            textAlign = TextAlign.Justify,
            maxLines = animateSize,
        )

        TextButton(
            onClick = { showFullDesc = !showFullDesc },
            contentPadding = PaddingValues(vertical = Dimensions.zero),
        ) {
            if (showFullDesc)
                Text(stringResource(R.string.desc_hide))
            else
                Text(stringResource(R.string.desc_show))
        }
    }
}
