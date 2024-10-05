package com.san.kir.features.accounts.shikimori.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.app.ComponentActivity
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenList
import com.san.kir.core.compose.topBar
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.ui.accountItem.ShikimoriListItem
import com.san.kir.features.accounts.shikimori.ui.accountRate.AccountRateScreen
import com.san.kir.features.accounts.shikimori.ui.accountScreen.AccountScreen
import com.san.kir.features.accounts.shikimori.ui.localItem.LocalItemScreen
import com.san.kir.features.accounts.shikimori.ui.localItems.LocalItemsScreen
import com.san.kir.features.accounts.shikimori.ui.search.ShikiSearchScreen
import timber.log.Timber

public fun ComponentActivity.setContent() {
    setContentView(
        ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    ShikimoriContent()
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShikimoriContent() {
    var nav: ShikiNavTarget by remember { mutableStateOf(ShikiNavTarget.LocalItems) }
    Timber.plant(Timber.DebugTree())

    Crossfade(targetState = nav, label = "") { target ->
        when (target) {
            ShikiNavTarget.Start ->
                ScreenList(
                    topBar = topBar(
                        navigationButton = NavigationButton.Back { },
                        title = stringResource(com.san.kir.data.models.R.string.accounts),
                    ),
                    additionalPadding = Dimensions.zero
                ) {
                    item(key = "Shiki") {
                        ShikimoriListItem { _, _ -> nav = ShikiNavTarget.Catalog }
                    }

                }

            ShikiNavTarget.Catalog ->
                AccountScreen(
                    accountId = 1,
                    navigateUp = {
                        nav = ShikiNavTarget.Start
                    },
                    navigateToShikiItem = { item, _ ->
                        Timber.v(item.toString())
                        nav = ShikiNavTarget.AccountRate(item)
                    },
                    navigateToLocalItems = { nav = ShikiNavTarget.LocalItems },
                    navigateToSearch = { /*nav = ShikiNavTarget.Search*/ }
                )

            is ShikiNavTarget.AccountRate -> {
                AccountRateScreen(
                    navigateUp = {
                        nav = ShikiNavTarget.Search
                    },
                    navigateToSearch = {},
                    accountId = 1,
                    mangaItem = target.item,
                )
            }

            ShikiNavTarget.Search -> {
                ShikiSearchScreen(
                    accountId = 1,
                    navigateUp = { nav = ShikiNavTarget.Start },
                    navigateToItem = { id, _ -> nav = ShikiNavTarget.AccountRate(id) },
                    searchText = "Fetish na Yuu",
                )
            }

            ShikiNavTarget.LocalItems -> {
                LocalItemsScreen(
                    accountId = 1,
                    navigateUp = { nav = ShikiNavTarget.Catalog },
                    navigateToItem = { id, _ -> nav = ShikiNavTarget.LocalItem(id) }
                )
            }

            is ShikiNavTarget.LocalItem -> {
                LocalItemScreen(
                    accountId = 1,
                    mangaId = target.id,
                    navigateUp = { nav = ShikiNavTarget.LocalItems },
                    navigateToSearch = {})
            }
        }
    }
}

internal sealed interface ShikiNavTarget {
    data object Start : ShikiNavTarget
    data object Catalog : ShikiNavTarget
    data class AccountRate(val item: AccountMangaItem) : ShikiNavTarget
    data object Search : ShikiNavTarget
    data object LocalItems : ShikiNavTarget
    data class LocalItem(val id: Long) : ShikiNavTarget
}
