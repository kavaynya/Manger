package com.san.kir.accounts.ui.accounts

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.san.kir.accounts.R
import com.san.kir.accounts.ui.catalogItem.CatalogItemScreen
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.barContainerColor
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.viewModel.rememberSendAction
import com.san.kir.core.utils.viewModel.rememberSendEvent
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.parsing.sites.AllhentaiConstants
import com.san.kir.data.parsing.sites.MintmangaConstants

private val selectedBarColor: Color
    @Composable
    get() = barContainerColor.copy(alpha = 0.95f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountsScreen(
    navigateUp: () -> Unit,
    navigateToShiki: (Long, SharedParams) -> Unit,
    navigateToBrowser: (String, String, SharedParams) -> Unit,
) {
    val holder: AccountsStateHolder = stateHolder { AccountsViewModel() }
    val state by holder.state.collectAsStateWithLifecycle()
    val sendAction = holder.rememberSendAction()
    val sendEvent = rememberSendEvent()

    ScreenList(
        additionalPadding = Dimensions.zero,
        /* updateAll = { sendEvent(UpdateEvent) }, */
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = stringResource(R.string.accounts)
        )
    ) {
        item("shiki") { ShikimoriListItem(navigateToShiki) }
        item("allhen") { CatalogItemScreen(AllhentaiConstants, navigateToBrowser) }
        item("mint") { CatalogItemScreen(MintmangaConstants, navigateToBrowser) }
    }
}
