package com.san.kir.library.ui.mangaAbout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.san.kir.core.compose.CheckBoxText
import com.san.kir.core.compose.DialogText
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.ImageWithStatus
import com.san.kir.core.compose.LabelText
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.animation.BottomAnimatedVisibility
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.browse
import com.san.kir.core.utils.formatDouble
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.base.Manga
import com.san.kir.library.R

@Composable
fun MangaAboutScreen(
    navigateUp: () -> Unit,
    itemId: Long,
) {
    val holder: MangaAboutStateHolder = stateHolder { MangaAboutViewModel() }
    val state by holder.state.collectAsState()

    LaunchedEffect(Unit) { holder.sendEvent(MangaAboutEvent.Set(itemId)) }

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.manga_info_dialog_title),
        ),
    ) {
        Column(
            modifier = Modifier
                .horizontalInsetsPadding()
                .bottomInsetsPadding()
        ) {
            Content(state.manga, state.categoryName, state.size, holder::sendEvent)
        }
    }

}

@Composable
private fun ColumnScope.Content(
    manga: Manga,
    categoryState: String,
    size: Double,
    sendEvent: (MangaAboutEvent) -> Unit,
) {
    val ctx = LocalContext.current

    LabelText(R.string.about_manga_dialog_name)
    DialogText(manga.name)

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_category)
    DialogText(categoryState)

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_authors)
    DialogText(manga.authorsList.toString().trim().removeSurrounding("[", "]"))

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_status_edition)
    DialogText(manga.status)

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_genres)
    DialogText(manga.genresList.toString().trim().removeSurrounding("[", "]"))

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_storage)
    DialogText(manga.path)

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_volume)
    DialogText(stringResource(R.string.library_page_item_size, formatDouble(size)))

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_link)
    DialogText(
        text = manga.host + manga.shortLink,
        color = Color.Cyan,
        onClick = { ctx.browse(manga.host + manga.shortLink) }
    )

    HalfSpacer()

    LabelText(R.string.add_manga_update)
    CheckBoxText(
        state = manga.isUpdate,
        onChange = { sendEvent(MangaAboutEvent.ChangeUpdate(it)) },
        firstTextId = R.string.add_manga_update_available
    )

    HalfSpacer()

    LabelText(R.string.add_manga_color)
    ColorPicker(manga.color) { sendEvent(MangaAboutEvent.ChangeColor(it)) }

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_about)
    DialogText(manga.about)

    HalfSpacer()

    LabelText(R.string.about_manga_dialog_logo)
    ImageWithStatus(manga.logo)
}

@Composable
private fun ColumnScope.ColorPicker(initialValue: Int, onValueChange: (Int) -> Unit) {
    val defaultColor = MaterialTheme.colors.primary

    var color by remember(initialValue) {
        mutableStateOf(
            runCatching { if (initialValue != 0) Color(initialValue) else null }
                .getOrNull() ?: defaultColor
        )
    }

    var picker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clickable { picker = !picker }
            .fillMaxWidth()
            .height(Dimensions.Image.small)
            .background(color)
    )

    BottomAnimatedVisibility(picker) {
        Column(modifier = Modifier.padding(horizontal = Dimensions.quarter)) {

            Slider("R", color.red) { color = color.copy(red = it) }
            Slider("G", color.green) { color = color.copy(green = it) }
            Slider("B", color.blue) { color = color.copy(blue = it) }
            Slider("A", color.alpha) { color = color.copy(alpha = it) }

            TextButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    onValueChange(color.toArgb())
                    picker = false
                },
            ) {
                Text(stringResource(R.string.about_manga_dialog_save))
            }
        }
    }
}

@Composable
private fun Slider(
    text: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.quarter),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimensions.half)
        )
    }
}
