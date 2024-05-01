package com.san.kir.catalog.ui.catalogItem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.san.kir.core.compose.HalfSpacer
import com.san.kir.core.compose.ToolbarProgress
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.browse
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.db.catalog.entities.DbSiteCatalogElement

@Composable
fun CatalogItemScreen(
    navigateUp: () -> Unit,
    navigateToAdd: (String, SharedParams) -> Unit,
    url: String,
) {
    val holder: CatalogItemViewModel = stateHolder { CatalogItemViewModel() }
    val state by holder.state.collectAsState()

    LaunchedEffect(Unit) { holder.sendAction(CatalogItemEvent.Set(url)) }

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
                                tint = MaterialTheme.colorScheme.onBackground,
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
                    .background(MaterialTheme.colorScheme.onError),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
        MangaInfoContent(state.item)
    }
}

@Composable
private fun MangaInfoContent(
    item: DbSiteCatalogElement,
) {
    val ctx = LocalContext.current

    LabelText(R.string.manga_info_dialog_name)
    DialogText(item.name)

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_authors)
    DialogText(item.authors.joinToString())

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_type)
    DialogText(item.type)

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_status_edition)
    DialogText(item.statusEdition)

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_volume)
    DialogText(stringResource(R.string.catalog_for_one_site_prefix_volume, item.volume))

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_status_translate)
    DialogText(item.statusTranslate)

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_genres)
    DialogText(item.genres.joinToString())

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_link)
    DialogText(
        item.link,
        color = if (MaterialTheme.colorScheme.isLight) Color.Blue else Color.Cyan
    ) { ctx.browse(item.link) }

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_about)
    DialogText(item.about)

    HalfSpacer()

    LabelText(R.string.manga_info_dialog_logo)
    ImageWithStatus(item.logo)
}
