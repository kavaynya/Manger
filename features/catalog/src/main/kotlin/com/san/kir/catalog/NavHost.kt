package com.san.kir.catalog

import NavEntry
import com.san.kir.catalog.ui.addOnline.AddOnlineScreen
import com.san.kir.catalog.ui.addStandart.AddStandartScreen
import com.san.kir.catalog.ui.catalog.CatalogScreen
import com.san.kir.catalog.ui.catalogItem.CatalogItemScreen
import com.san.kir.catalog.ui.catalogs.CatalogsScreen
import com.san.kir.catalog.ui.search.SearchScreen
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.circleShapeAnimator
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.compose.animation.verticalSlide
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.data.models.catalog.SiteCatalogElement
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

public val catalogsSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

@NavEntry
@Serializable
public data object Catalogs : NavConfig() {

    internal val creator = navCreator<Catalogs> { _ ->
        CatalogsScreen(
            navigateUp = backPressed(),
            navigateToSearch = add(GlobalSearch()),
            navigateToItem = add(::Catalog)
        )
    }

    internal val animation = navAnimation<Catalogs> { horizontalSlide() }
}

@NavEntry
@Serializable
public data class Catalog(
    internal val catalogName: String,
    internal val params: SharedParams,
) : NavConfig() {
    internal companion object {
        val creator = navCreator<Catalog> { config ->
            CatalogScreen(
                navigateUp = backPressed(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                catalogName = config.catalogName
            )
        }

        val animation = navAnimation<Catalog> { shapeAnimator(it.params) }
    }
}

@NavEntry
@Serializable
public class Info(internal val item: SiteCatalogElement, internal val params: SharedParams) :
    NavConfig() {
    internal companion object {
        val creator = navCreator<Info> { config ->
            CatalogItemScreen(
                navigateUp = backPressed(),
                navigateToAdd = add(::AddLocal),
                item = config.item
            )
        }

        val animation = navAnimation<Info> { shapeAnimator(it.params) }
    }
}

@NavEntry
@Serializable
public class AddLocal(internal val url: String, internal val params: SharedParams) : NavConfig() {
    internal companion object {
        val creator = navCreator<AddLocal> { config ->
            AddStandartScreen(
                navigateUp = backPressed(),
                url = config.url
            )
        }

        val animation = navAnimation<AddLocal> { shapeAnimator(it.params) }
    }
}

@NavEntry
@Serializable
public class GlobalSearch(internal val query: String = "") : NavConfig() {
    internal companion object {
        val creator = navCreator<GlobalSearch> { config ->
            SearchScreen(
                navigateUp = backPressed(),
                navigateToInfo = add(::Info),
                navigateToAdd = add(::AddLocal),
                searchText = config.query
            )
        }

        val animation = navAnimation<GlobalSearch> { verticalSlide() }
    }
}

@NavEntry
@Serializable
public class AddOnline(internal val params: SharedParams) : NavConfig() {
    internal companion object {
        val creator = navCreator<AddOnline> { _ ->
            AddOnlineScreen(
                navigateUp = backPressed(),
                navigateToNext = add(::AddLocal),
            )
        }

        val animation = navAnimation<AddOnline> { circleShapeAnimator(it.params) }
    }
}
