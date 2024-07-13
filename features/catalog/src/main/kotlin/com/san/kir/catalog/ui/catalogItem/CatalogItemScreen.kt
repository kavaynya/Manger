package com.san.kir.catalog.ui.catalogItem

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.san.kir.catalog.R
import com.san.kir.core.compose.DefaultSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.FullWeightSpacer
import com.san.kir.core.compose.ImageWithStatus
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.ToolbarProgress
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.bottomInsetsPadding
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.browse
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.catalog.SiteCatalogElement


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogItemScreen(
    navigateUp: () -> Unit,
    navigateToAdd: (String, SharedParams) -> Unit,
    item: SiteCatalogElement,
) {
    val holder: CatalogItemViewModel = stateHolder { CatalogItemViewModel(item) }
    val state by holder.state.collectAsStateWithLifecycle()

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = state.item.name,
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
                            ) { navigateToAdd(item.link, params) }
                        }

                        ContainingInLibraryState.Ok -> {}
                    }
                }
            },
            hasAction = state.background is BackgroundState.Load
        ),
    ) {
        Column(
            modifier = Modifier
                .horizontalInsetsPadding()
                .bottomInsetsPadding()
                .padding(Dimensions.default)
        ) {

        }

        TopAnimatedVisibility(visible = state.background is BackgroundState.Error) {
            Text(
                stringResource((state.background as BackgroundState.Error).text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.half)
                    .background(MaterialTheme.colorScheme.onError),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }

        TopAnimatedVisibility(visible = state.item.about.isNotEmpty()) {
            Column {
                Label(R.string.description)
                Description(state.item.about)
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.authors.all { it.isEmpty() }.not()) {
            Column {
                Label(R.string.authors)
                Title(state.item.authors.joinToString())
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.genres.isNotEmpty()) {
            Column {
                Label(R.string.genres)
                Title(state.item.genres.joinToString())
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.background is BackgroundState.None) {
            Column {
                Row {
                    TopAnimatedVisibility(visible = state.item.type.isNotEmpty()) {
                        Column {
                            Label(R.string.type)
                            Title(state.item.type)
                        }
                    }

                    FullWeightSpacer()

                    Column {
                        Label(R.string.volume)
                        Title(stringResource(R.string.chapters_format, state.item.volume))
                    }
                }

                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.statusEdition.isNotEmpty()) {
            Column {
                Label(R.string.edition_status)
                Title(state.item.statusEdition)
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.statusTranslate.isNotEmpty()) {
            Column {
                Label(R.string.translate_status)
                Title(state.item.statusTranslate)
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.link.isNotEmpty()) {
            Column {
                val ctx = LocalContext.current

                Label(R.string.source_link)
                Title(
                    state.item.link,
                    Modifier.clickable { ctx.browse(state.item.link) },
                    Color.Cyan
                )
                DefaultSpacer()
            }
        }

        TopAnimatedVisibility(visible = state.item.logo.isNotEmpty()) {
            Column {
                Label(R.string.logo)
                ImageWithStatus(
                    state.item.logo,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                DefaultSpacer()
            }
        }
    }
}

@Composable
private fun Label(idRes: Int) {
    Text(
        text = stringResource(idRes),
        modifier = Modifier.padding(start = Dimensions.default),
        fontSize = 14.sp,
        fontStyle = FontStyle.Italic,
    )
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun Description(description: String) {

    var showFullDesc by remember { mutableStateOf(false) }
    val animateSize = animateIntAsState(if (showFullDesc) 100 else 7, label = "")

    Column {
        Text(
            text = description,
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            maxLines = animateSize.value
        )

        TextButton(
            onClick = { showFullDesc = !showFullDesc },
            contentPadding = PaddingValues(vertical = Dimensions.zero)
        ) {
            if (showFullDesc) {
                Text(stringResource(R.string.desc_hide))
            } else {
                Text(stringResource(R.string.desc_show))
            }
        }
    }
}
