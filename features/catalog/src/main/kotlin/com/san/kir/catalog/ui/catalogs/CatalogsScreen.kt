package com.san.kir.catalog.ui.catalogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.san.kir.catalog.R
import com.san.kir.core.compose.CircleLogo
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.QuarterSpacer
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.animation.FromEndToEndAnimContent
import com.san.kir.core.compose.animation.FromStartToStartAnimContent
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.rememberSharedParams
import com.san.kir.core.compose.animation.saveParams
import com.san.kir.core.compose.startInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.append
import com.san.kir.core.utils.findInGoogle
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CatalogsScreen(
    navigateUp: () -> Unit,
    navigateToItem: (String, SharedParams) -> Unit,
    navigateToSearch: () -> Unit,
) {
    val holder: CatalogsStateHolder = stateHolder { CatalogsViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()

    ScreenList(
        topBar = topBar(
            title = stringResource(R.string.catalogs),
            actions = {
                MenuIcon(icon = Icons.Default.Search, onClick = navigateToSearch)
                ExpandedMenu()
            },
            navigationButton = NavigationButton.Back(navigateUp),
            hasAction = state.background
        ),
        menuActions = {
            MenuText(R.string.update_catalog_contain) {
                sendAction(CatalogsAction.UpdateContent)
            }
        },
        additionalPadding = Dimensions.half,
    ) {
        items(items = state.items, key = { it.hashCode() }) { item ->
            ItemView(item) { navigateToItem(item.name, it) }
        }

    }
}

@Composable
private fun ItemView(item: CheckableSite, onClick: (SharedParams) -> Unit) {
    val params = rememberSharedParams()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(params) })
            .saveParams(params)
            .padding(vertical = Dimensions.half, horizontal = Dimensions.default)
    ) {

        CircleLogo(
            logoUrl = findInGoogle(item.host),
            modifier = Modifier.startInsetsPadding(),
            size = Dimensions.Image.default
        )

        Column(
            modifier = Modifier
                .weight(1f, true)
                .padding(start = Dimensions.default)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                buildAnnotatedString {
                    val spanStyle = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    append(item.name, spanStyle)
                    append(" (", spanStyle)
                    append(item.host, SpanStyle(fontSize = 13.sp))
                    append(")", spanStyle)
                },
                overflow = TextOverflow.Ellipsis,
            )

            QuarterSpacer()

            Row(verticalAlignment = Alignment.CenterVertically) {
                SiteState(item.state)
                Text(
                    stringResource(R.string.volume),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
                VolumeState(item.volume)
            }
        }
    }
}

@Composable
private fun SiteState(state: SiteState) {
    FromStartToStartAnimContent(targetState = state) {
        when (it) {
            SiteState.Error ->
                Icon(
                    Icons.Default.Error, "",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = Dimensions.half).size(Dimensions.default),
                )

            SiteState.Load ->
                CircularProgressIndicator(
                    strokeWidth = Dimensions.smallest,
                    modifier = Modifier
                        .padding(end = Dimensions.half)
                        .size(Dimensions.default)
                )

            SiteState.Ok -> {}
        }
    }
}

@Composable
private fun VolumeState(state: VolumeState) {
    FromEndToEndAnimContent(targetState = state) {
        when (it) {
            VolumeState.Error ->
                Text(
                    text = " " + stringResource(R.string.error, SpanStyle()),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )

            VolumeState.Load -> {
                Text(
                    text = " " + stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
            }

            is VolumeState.Ok -> {
                Text(
                    text = buildAnnotatedString {
                        append(" ")
                        append("${it.volume}", SpanStyle(color = MaterialTheme.colorScheme.tertiary))

                        if (it.diff != 0) {
                            append(if (it.isPositive) " + " else " - ")
                            append("${it.diff}", SpanStyle(color = MaterialTheme.colorScheme.secondary))
                        }

                        stringResource(R.string.volume_format, it.volume)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
