package com.san.kir.features.accounts.shikimori

import NavEntry
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.circleShapeAnimator
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.animation.verticalSlide
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.accounts.shikimori.ui.accountRate.AccountRateScreen
import com.san.kir.features.accounts.shikimori.ui.accountScreen.AccountScreen
import com.san.kir.features.accounts.shikimori.ui.localItem.LocalItemScreen
import com.san.kir.features.accounts.shikimori.ui.localItems.LocalItemsScreen
import com.san.kir.features.accounts.shikimori.ui.search.ShikiSearchScreen
import kotlinx.serialization.Serializable

public fun shikimoriNavigationCreators() {
    AddNavigationCreators
}

@Serializable
@NavEntry
public data class Shikimori(
    internal val accountId: Long,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<Shikimori> { config ->
            AccountScreen(
                accountId = config.accountId,
                navigateUp = backPressed(),
                navigateToShikiItem = add(::ProfileItem),
                navigateToLocalItems = add(::LocalItems),
                navigateToSearch = add(Search(config.accountId))
            )
        }

        val animation = navAnimation<Shikimori> { config -> itemShapeAnimator(config.params) }
    }
}

@Serializable
@NavEntry
public class LocalItems(
    internal val accountId: Long,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<LocalItems> { config ->
            LocalItemsScreen(
                accountId = config.accountId,
                navigateUp = backPressed(),
                navigateToItem = add(::LocalItem)
            )
        }

        val animation = navAnimation<LocalItems> { config -> circleShapeAnimator(config.params) }
    }
}

@Serializable
@NavEntry
public class LocalItem(
    internal val accountId: Long,
    internal val id: Long,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<LocalItem> { config ->
            LocalItemScreen(
                accountId = config.accountId,
                mangaId = config.id,
                navigateUp = backPressed(),
                navigateToSearch = add(::Search)
            )
        }

        val animation = navAnimation<LocalItem> { config -> shapeAnimator(config.params) }
    }
}

@Serializable
@NavEntry
public class Search(
    internal val accountId: Long,
    internal val query: String = "",
) : NavConfig() {
    internal companion object {
        val creator = navCreator<Search> { config ->
            ShikiSearchScreen(
                accountId = config.accountId,
                navigateUp = backPressed(),
                navigateToItem = add(::ProfileItem),
                searchText = config.query
            )
        }

        val animation = navAnimation<Search> { verticalSlide() }
    }
}

@Serializable
@NavEntry
public class ProfileItem(
    internal val accountId: Long,
    internal val mangaId: Long,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<ProfileItem> { config ->
            AccountRateScreen(
                navigateUp = backPressed(),
                navigateToSearch = add(::CatalogSearch),
                accountId = config.accountId,
                mangaId = config.mangaId,
            )
        }

        val animation = navAnimation<ProfileItem> { config -> shapeAnimator(config.params) }
    }
}
