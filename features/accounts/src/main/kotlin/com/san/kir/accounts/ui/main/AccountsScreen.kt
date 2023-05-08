package com.san.kir.accounts.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.topBar
import com.san.kir.core.support.R
import com.san.kir.features.catalogs.allhen.ui.allhen.AllhenItemScreen as AllHenItem
import com.san.kir.features.shikimori.ui.accountItem.AccountItem as ShikiItem

@Composable
internal fun AccountsScreen(
    navigateUp: () -> Unit,
    navigateToShiki: (SharedParams) -> Unit,
    navigateToBrowser: (String, SharedParams) -> Unit,
) {
    ScreenList(
        topBar = topBar(
            title = stringResource(R.string.accounts),
            navigationButton = NavigationButton.Back(navigateUp)
        ),
        additionalPadding = Dimensions.zero
    ) {
        item(key = "Shiki") { ShikiItem(navigateToShiki) }
        item(key = "allhen") { AllHenItem(navigateToBrowser) }
//        item(key = "comx") { ComXItem(navigateToBrowser) }
    }
}
