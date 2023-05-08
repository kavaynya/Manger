package com.san.kir.catalog.ui.catalogItem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.san.kir.catalog.R
import com.san.kir.core.compose.DialogText
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.ImageWithStatus
import com.san.kir.core.compose.LabelText
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.SmallSpacer
import com.san.kir.core.compose.ToolbarProgress
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.browse
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.base.SiteCatalogElement

@Composable
fun CatalogItemScreen(
    navigateUp: () -> Unit,
    navigateToAdd: (String, SharedParams) -> Unit,
    url: String,
) {
    val holder: CatalogItemViewModel = stateHolder { CatalogItemViewModel() }
    val state by holder.state.collectAsState()

    LaunchedEffect(Unit) { holder.sendEvent(CatalogItemEvent.Set(url)) }

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.manga_info_dialog_title),
            actions = {
                FromEndToEndAnimContent(state.containingInLibrary) {
                    when (it) {
                        ContainingInLibraryState.Check -> ToolbarProgress()
                        ContainingInLibraryState.None -> {
                            val params = rememberSharedParams(fromCenter = true)
                            MenuIcon(
                                icon = Icons.Default.Add,
                                tint = MaterialTheme.colors.onBackground,
                                modifier = Modifier.saveParams(params)
                            ) { navigateToAdd(url, params) }
                        }

                        ContainingInLibraryState.Ok -> {}
                    }
                }
            },
            hasAction = state.background is BackgroundState.Load
        ),
    ) {
        TopAnimatedVisibility(visible = state.background is BackgroundState.Error) {
            Text(
                stringResource(R.string.manga_info_dialog_update_failed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.half)
                    .background(MaterialTheme.colors.onError),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error
            )
        }
        MangaInfoContent(state.item)
    }
}

@Composable
private fun ColumnScope.MangaInfoContent(
    item: SiteCatalogElement,
) {
    val ctx = LocalContext.current

    LabelText(R.string.manga_info_dialog_name)
    DialogText(item.name)

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_authors)
    DialogText(item.authors.joinToString())

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_type)
    DialogText(item.type)

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_status_edition)
    DialogText(item.statusEdition)

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_volume)
    DialogText(stringResource(R.string.catalog_for_one_site_prefix_volume, item.volume))

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_status_translate)
    DialogText(item.statusTranslate)

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_genres)
    DialogText(item.genres.joinToString())

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_link)
    DialogText(
        item.link,
        color = if (MaterialTheme.colors.isLight) Color.Blue else Color.Cyan
    ) { ctx.browse(item.link) }

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_about)
    DialogText(item.about)

    SmallSpacer()

    LabelText(R.string.manga_info_dialog_logo)
    ImageWithStatus(item.logo)
}
