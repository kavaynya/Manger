package com.san.kir.features.shikimori

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.catalog.CatalogsNavHost
import com.san.kir.catalog.GlobalSearch
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.circleShapeAnimator
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.animation.verticalSlide
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.features.shikimori.ui.accountRate.AccountRateScreen
import com.san.kir.features.shikimori.ui.accountScreen.AccountScreen
import com.san.kir.features.shikimori.ui.localItem.LocalItemScreen
import com.san.kir.features.shikimori.ui.localItems.LocalItemsScreen
import com.san.kir.features.shikimori.ui.search.ShikiSearchScreen
import kotlinx.parcelize.Parcelize

@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> {
            AccountScreen(
                navigateUp = backPressed(),
                navigateToShikiItem = add(::ProfileItem),
                navigateToLocalItems = add(::LocalItems),
                navigateToSearch = add(Search())
            )
        }
    }
}

@Parcelize
internal class LocalItems(val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<LocalItems> {
            LocalItemsScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::LocalItem)
            )
        }
    }
}

@Parcelize
internal class LocalItem(val id: Long, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<LocalItem> { config ->
            LocalItemScreen(
                mangaId = config.id,
                navigateUp = backPressed(),
                navigateToSearch = add(::Search)
            )
        }
    }
}

@Parcelize
internal class Search(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<Search> { config ->
            ShikiSearchScreen(
                navigateUp = backPressed(),
                navigateToItem = add(::ProfileItem),
                searchText = config.query
            )
        }
    }
}

@Parcelize
internal class ProfileItem(val mangaId: Long, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<ProfileItem> { config ->
            AccountRateScreen(
                navigateUp = backPressed(),
                navigateToSearch = add(::CatalogSearch),
                mangaId = config.mangaId,
            )
        }
    }
}

@Parcelize
internal class CatalogSearch(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<CatalogSearch> { config ->
            CatalogsNavHost(GlobalSearch(config.query))
        }
    }
}

@Composable
fun ShikimoriNavHost() {
    NavHost(Main(), animation) {
        when (it) {
            is Main -> Main.creator(it)
            is LocalItems -> LocalItems.creator(it)
            is LocalItem -> LocalItem.creator(it)
            is Search -> Search.creator(it)
            is ProfileItem -> ProfileItem.creator(it)
            is CatalogSearch -> CatalogSearch.creator(it)
            else -> null
        }
    }
}

private val animation = stackAnimation<NavConfig, NavContainer> { initial, target, direction ->
    if (direction.isFront) frontAnimation(initial.configuration)
    else frontAnimation(target.configuration)
}

private fun frontAnimation(config: NavConfig): StackAnimator {
    return when (config) {
        is ProfileItem -> shapeAnimator(config.params)
        is LocalItems -> circleShapeAnimator(config.params)
        is LocalItem -> shapeAnimator(config.params)
        is Search, is CatalogSearch -> verticalSlide()
        else -> EmptyStackAnimator
    }
}
