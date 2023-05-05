package com.san.kir.features.catalogs.allhen.ui.accountScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.parsing.sites.Allhentai

@Composable
fun AccountScreen(
    navigateUp: () -> Unit,
    url: String?,
) {
    val holder: AccountScreenStateHolder = stateHolder { AccountScreenViewModel() }
    val state by holder.state.collectAsState()

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = Allhentai.SITE_NAME
        )
    ) {
        WebView(
            state = rememberWebViewState(url ?: ""),
            modifier = Modifier.fillMaxSize(),
            onCreated = { view ->
                view.settings.javaScriptEnabled = true
            },
        )
    }
}
