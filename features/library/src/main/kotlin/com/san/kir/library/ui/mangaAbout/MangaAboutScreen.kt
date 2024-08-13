package com.san.kir.library.ui.mangaAbout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.ImageWithStatus
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.animateToDelayed
import com.san.kir.core.compose.animation.rememberDoubleAnimatable
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.browse
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.format
import com.san.kir.core.utils.navigation.rememberLambda
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.authorsStr
import com.san.kir.data.models.main.genresStr
import com.san.kir.library.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MangaAboutScreen(
    navigateUp: () -> Unit,
    itemId: Long,
) {
    val holder: MangaAboutStateHolder = stateHolder { MangaAboutViewModel(itemId) }
    val state by holder.state.collectAsStateWithLifecycle()
    val changeUpdate =
        rememberLambda { state: Boolean -> holder.sendAction(MangaAboutAction.ChangeUpdate(state)) }
    val size = rememberDoubleAnimatable(0.0)

    LaunchedEffect(state.size) { size.animateToDelayed(state.size, 300, 0) }

    ScreenContent(
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = state.manga.name,
        ),
    ) {
        Column(
            modifier = Modifier
                .horizontalInsetsPadding()
                .bottomInsetsPadding()
                .padding(Dimensions.default)
        ) {
            TopAnimatedVisibility(state.manga.about.isNotEmpty()) {
                Column {
                    Label(R.string.about)
                    Title(state.manga.about)
                    DefaultSpacer()
                }
            }

            TopAnimatedVisibility(state.manga.genresList.all { it.isBlank() }.not()) {
                Column {
                    Label(R.string.genres)
                    Title(state.manga.genresStr)
                    DefaultSpacer()
                }
            }

            Label(R.string.status_edition)
            Title(state.manga.status)
            DefaultSpacer()

            TopAnimatedVisibility(state.manga.authorsList.isNotEmpty()) {
                Column {
                    Label(R.string.authors)
                    Title(state.manga.authorsStr)
                    DefaultSpacer()
                }
            }

            Label(R.string.storage_path)
            Title(state.manga.path)
            DefaultSpacer()

            Label(R.string.current_volume)
            Title(stringResource(R.string.size_format, size.value.format()))
            DefaultSpacer()

            val ctx = LocalContext.current
            Label(R.string.source_link)
            Title(
                "${state.manga.host}${state.manga.shortLink}",
                modifier = Modifier.clickable {
                    ctx.browse("${state.manga.host}${state.manga.shortLink}")
                }
            )
            DefaultSpacer()

            Label(R.string.category)
            Title(state.categoryName)
            DefaultSpacer()

            UpdateToggle(state.manga.isUpdate, changeUpdate)
            DefaultSpacer()

            Label(R.string.logo)
            ImageWithStatus(
                url = state.manga.logo,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}

@Composable
private fun Label(idRes: Int) {
    Text(
        text = stringResource(idRes),
        modifier = Modifier.padding(start = Dimensions.default),
        fontSize = 14.sp,
        fontStyle = FontStyle.Italic
    )
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun UpdateToggle(value: Boolean, onToggleChange: (Boolean) -> Unit) {
    val alpha by animateFloatAsState(if (value) 1.0f else 0.5f, label = "")

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.half),
    ) {
        Switch(checked = value, onCheckedChange = onToggleChange)
        Text(
            text = stringResource(R.string.update_available),
            modifier = Modifier.alpha(alpha)
        )
    }
}

