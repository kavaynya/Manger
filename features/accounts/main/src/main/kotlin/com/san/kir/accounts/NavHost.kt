package com.san.kir.accounts

import NavEntry
import com.san.kir.accounts.ui.accounts.AccountsScreen
import com.san.kir.accounts.ui.authBrowser.AuthBrowserScreen
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.accounts.shikimori.Shikimori
import com.san.kir.features.accounts.shikimori.shikimoriNavigationCreators
import kotlinx.serialization.Serializable

public fun accountsNavigationCreators() {
    AddNavigationCreators
    shikimoriNavigationCreators()
}

@NavEntry
@Serializable
public data object Accounts : NavConfig() {
    internal val creator = navCreator<Accounts> {
        AccountsScreen(
            navigateUp = backPressed(),
            navigateToShiki = add(::Shikimori),
            navigateToBrowser = add(::AuthBrowser)
        )
    }

    internal val animation = navAnimation<Accounts> { horizontalSlide() }
}

@NavEntry
@Serializable
public class AuthBrowser(
    internal val url: String,
    internal val title: String,
    internal val params: SharedParams
) : NavConfig() {
    internal companion object {
        val creator = navCreator<AuthBrowser> { config ->
            AuthBrowserScreen(
                navigateUp = backPressed(),
                url = config.url,
                title = config.title,
            )
        }

        val animation = navAnimation<AuthBrowser> { itemShapeAnimator(it.params) }
    }
}
