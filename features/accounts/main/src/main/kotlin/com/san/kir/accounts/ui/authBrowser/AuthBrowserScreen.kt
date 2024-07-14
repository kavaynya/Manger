package com.san.kir.accounts.ui.authBrowser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.stateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthBrowserScreen(
    navigateUp: () -> Unit,
    url: String,
    title: String,
) {
    val holder: AuthBrowserStateHolder = stateHolder { AuthBrowserViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()

    val webState = rememberWebViewState(url)

    ScreenContent(
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = title,
            hasAction = webState.isLoading,
            progressAction = (webState.loadingState as? LoadingState.Loading)?.progress
        )
    ) {
        WebView(
            state = webState,
            modifier = Modifier.fillMaxSize(),
            onCreated = { view ->
                view.settings.javaScriptEnabled = true
                view.settings.userAgentString =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
            },
            captureBackPresses = false
        )
    }
}
