package com.san.kir.features.accounts.shikimori

import NavEntry
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.circleShapeAnimator
import com.san.kir.core.compose.animation.itemShapeAnimator
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.animation.verticalSlide
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.accounts.shikimori.logic.models.AccountMangaItem
import com.san.kir.features.accounts.shikimori.ui.accountRate.AccountRateScreen
import com.san.kir.features.accounts.shikimori.ui.accountScreen.AccountScreen
import com.san.kir.features.accounts.shikimori.ui.localItem.LocalItemScreen
import com.san.kir.features.accounts.shikimori.ui.localItems.LocalItemsScreen
import com.san.kir.features.accounts.shikimori.ui.search.ShikiSearchScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

public val shikimoriSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

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
                navigateToShikiItem = add { mangaItem, params ->
                    ProfileItem(config.accountId, mangaItem, params)
                },
                navigateToLocalItems = add { params -> LocalItems(config.accountId, params) },
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
                navigateToItem = add { id, params -> LocalItem(config.accountId, id, params) }
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
                navigateToSearch = add { query -> Search(config.accountId, query) }
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
                navigateToItem = add { mangaItem, params ->
                    ProfileItem(config.accountId, mangaItem, params)
                },
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
    internal val mangaItem: AccountMangaItem,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<ProfileItem> { config ->
            AccountRateScreen(
                navigateUp = backPressed(),
                navigateToSearch = add { query -> Search(config.accountId, query) },
                accountId = config.accountId,
                mangaItem = config.mangaItem,
            )
        }

        val animation = navAnimation<ProfileItem> { config -> shapeAnimator(config.params) }
    }
}
