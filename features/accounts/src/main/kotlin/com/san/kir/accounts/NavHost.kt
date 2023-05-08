package com.san.kir.accounts

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.essenty.parcelable.Parcelize
import com.san.kir.accounts.ui.main.AccountsScreen
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.catalogs.allhen.ui.accountScreen.AccountScreen
import com.san.kir.features.shikimori.ShikimoriNavHost

@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            AccountsScreen(
                navigateUp = backPressed(),
                navigateToShiki = add(::Shikimori),
                navigateToBrowser = add(::Allhen)
            )
        }
    }
}

@Parcelize
internal class Shikimori(val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Shikimori> {
            ShikimoriNavHost()
        }
    }
}

@Parcelize
internal class Allhen(val url: String, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Allhen> { config ->
            AccountScreen(
                navigateUp = backPressed(),
                url = config.url
            )
        }
    }
}

@Composable
fun AccountsNavHost() {
    NavHost(
        startConfig = Main(),
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Shikimori -> Shikimori.creator(config)
            is Allhen -> Allhen.creator(config)
            else -> null
        }
    }
}

private val animation = stackAnimation<NavConfig, NavContainer> { initial, target, direction ->
    if (direction.isFront) frontAnimation(initial.configuration)
    else frontAnimation(target.configuration)
}

private fun frontAnimation(initial: NavConfig): StackAnimator {
    return when (initial) {
        is Shikimori -> itemShapeAnimator(initial.params)
        is Allhen -> itemShapeAnimator(initial.params, )
        else -> EmptyStackAnimator
    }
}
