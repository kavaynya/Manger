package com.san.kir.manger.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.san.kir.features.catalogs.allhen.ui.accountScreen.AccountScreen
import com.san.kir.manger.navigation.utils.NavTarget
import com.san.kir.manger.navigation.utils.navTarget
import com.san.kir.manger.navigation.utils.navigation
import com.san.kir.manger.ui.accounts.AccountsScreen

enum class AccountsNavTarget : NavTarget {
    Main {
        override val content = navTarget(route = GraphTree.Accounts.main) {
            AccountsScreen(
                navigateUp = navigateUp(),
                navigateToShiki = rememberNavigate(Shikimori),
                navigateToBrowser = rememberNavigateString(Allhen)
            )
        }
    },
    Shikimori {
        override val content = navTarget(route = GraphTree.Accounts.Shikimori())
    },
    Allhen {
        override val content =
            navTarget(route = GraphTree.Accounts.Catalogs.allhen, hasItems = true) {
                AccountScreen(
                    navigateUp = navigateUp(),
                    url = stringElement(),
                )
            }
    };
}

private val targets = AccountsNavTarget.values().toList()

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.accountsNavGraph(nav: NavHostController) {
    navigation(
        nav = nav,
        startDestination = AccountsNavTarget.Main,
        route = MainNavTarget.Accounts,
        targets = targets
    ) {
        accountShikimoriNavGraph(nav)
    }
}
