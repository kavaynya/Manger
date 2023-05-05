package com.san.kir.accounts

import androidx.compose.runtime.Composable
import com.arkivanov.essenty.parcelable.Parcelize
import com.san.kir.accounts.ui.main.AccountsScreen
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.catalogs.allhen.ui.accountScreen.AccountScreen
import com.san.kir.features.shikimori.ShikimoriNavHost

@Parcelize
internal class MainConfig : NavConfig {
    companion object {
        val creator = navCreator<MainConfig> {
            AccountsScreen(
                navigateUp = backPressed(),
                navigateToShiki = add(ShikimoriConfig()),
                navigateToBrowser = add(::AllhenConfig)
            )
        }
    }
}

@Parcelize
internal class ShikimoriConfig : NavConfig {
    companion object {
        val creator = navCreator<ShikimoriConfig> {
            ShikimoriNavHost()
        }
    }
}

@Parcelize
internal class AllhenConfig(val url: String) : NavConfig {
    companion object {
        val creator = navCreator<AllhenConfig> { config ->
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
        startConfig = MainConfig(),
        animation = null,
    ) { config ->
        when (config) {
            is MainConfig -> MainConfig.creator(config)
            is ShikimoriConfig -> ShikimoriConfig.creator(config)
            is AllhenConfig -> AllhenConfig.creator(config)
            else -> null
        }
    }
}
