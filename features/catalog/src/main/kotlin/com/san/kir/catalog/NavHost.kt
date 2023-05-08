package com.san.kir.catalog

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.catalog.ui.addOnline.AddOnlineScreen
import com.san.kir.catalog.ui.addStandart.AddStandartScreen
import com.san.kir.catalog.ui.catalog.CatalogScreen
import com.san.kir.catalog.ui.catalogItem.CatalogItemScreen
import com.san.kir.catalog.ui.catalogs.CatalogsScreen
import com.san.kir.catalog.ui.search.SearchScreen
import com.san.kir.core.compose.animation.EmptyStackAnimator
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.animation.verticalSlide
import com.san.kir.core.compose.backPressed
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.NavContainer
import com.san.kir.core.utils.navigation.NavHost
import com.san.kir.core.utils.navigation.navCreator
import kotlinx.parcelize.Parcelize

// hasDeepLink = true
@Parcelize
internal class Main : NavConfig {
    companion object {
        val creator = navCreator<Main> { _ ->
            CatalogsScreen(
                navigateUp = backPressed(),
                navigateToSearch = add(GlobalSearch()),
                navigateToItem = add(::Catalog)
            )
        }
    }
}

@Parcelize
internal class Catalog(val catalogName: String, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Catalog> { config ->
            CatalogScreen(
                navigateUp = back(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                catalogName = config.catalogName
            )
        }
    }
}

@Parcelize
internal class Info(val url: String, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<Info> { config ->
            CatalogItemScreen(
                navigateUp = backPressed(),
                navigateToAdd = add(::AddLocal),
                url = config.url
            )
        }
    }
}

@Parcelize
internal class AddLocal(val url: String, val params: SharedParams) : NavConfig {
    companion object {
        val creator = navCreator<AddLocal> { config ->
            AddStandartScreen(
                navigateUp = backPressed(),
                url = config.url
            )
        }
    }
}

@Parcelize
class GlobalSearch(val query: String = "") : NavConfig {
    companion object {
        val creator = navCreator<GlobalSearch> { config ->
            SearchScreen(
                navigateUp = backPressed(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                searchText = config.query
            )
        }
    }
}

@Parcelize
class AddOnline : NavConfig {
    companion object {
        val creator = navCreator<AddOnline> { _ ->
            AddOnlineScreen(
                navigateUp = backPressed(),
                navigateToNext = add(::AddLocal),
            )
        }
    }
}

@Composable
fun CatalogsNavHost(startConfig: NavConfig = Main()) {
    NavHost(
        startConfig = startConfig,
        animation = animation,
    ) { config ->
        when (config) {
            is Main -> Main.creator(config)
            is Catalog -> Catalog.creator(config)
            is GlobalSearch -> GlobalSearch.creator(config)
            is Info -> Info.creator(config)
            is AddLocal -> AddLocal.creator(config)
            is AddOnline -> AddOnline.creator(config)
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
        is GlobalSearch -> verticalSlide()
        is Catalog -> shapeAnimator(config.params)
        is Info -> shapeAnimator(config.params)
        is AddLocal -> shapeAnimator(config.params)
        else -> EmptyStackAnimator
    }
}
