package com.san.kir.manger.ui.application_navigation.catalog

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.san.kir.manger.ui.application_navigation.additional_manga_screens.MangaAddScreen
import com.san.kir.manger.ui.application_navigation.additional_manga_screens.MangaInfoScreen
import com.san.kir.manger.ui.application_navigation.additional_manga_screens.siteCatalogItemViewModel
import com.san.kir.manger.ui.application_navigation.catalog.catalog.CatalogScreen
import com.san.kir.manger.ui.application_navigation.catalog.catalog.catalogViewModel
import com.san.kir.manger.ui.application_navigation.catalog.global_search.GlobalSearchScreen
import com.san.kir.manger.ui.application_navigation.catalog.main.CatalogsScreen
import com.san.kir.manger.ui.utils.NavItem
import com.san.kir.manger.ui.utils.NavTarget
import com.san.kir.manger.ui.utils.SiteCatalogItem
import com.san.kir.manger.ui.utils.SiteItem
import com.san.kir.manger.ui.utils.getElement

sealed class CatalogsNavTarget : NavTarget {

    object Main : CatalogsNavTarget() {
        override val route: String = "main"
    }

    object Catalog : CatalogsNavTarget() {
        override val base: String = "catalog"
        override val item: NavItem = SiteItem
    }

    object GlobalSearch : CatalogsNavTarget() {
        override val route: String = "global_search"
    }

    object Info : CatalogsNavTarget() {
        override val base: String = "info"
        override val item: NavItem = SiteCatalogItem
    }

    object AddLocal : CatalogsNavTarget() {
        override val base: String = "add_local"
        override val item: NavItem = SiteCatalogItem
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.catalogsNavGraph(nav: NavHostController) {
    composable(
        route = CatalogsNavTarget.Main.route,
        content = {
            CatalogsScreen(nav)
        }
    )

    composable(
        route = CatalogsNavTarget.Catalog.route,
        content = {
            val item = nav.getElement(SiteItem) ?: ""
            val viewModel = catalogViewModel(item)

            CatalogScreen(nav, viewModel)
        }
    )

    composable(
        route = CatalogsNavTarget.GlobalSearch.route,
        content = {
            GlobalSearchScreen(nav)
        }
    )

    composable(
        route = CatalogsNavTarget.Info.route,
        content = {
            val item = nav.getElement(SiteCatalogItem) ?: ""
            val viewModel = siteCatalogItemViewModel(url = item)

            val element by viewModel.item.collectAsState()

            MangaInfoScreen(nav, element)
        }
    )

    composable(
        route = CatalogsNavTarget.AddLocal.route,
        content = {
            val item = nav.getElement(SiteCatalogItem) ?: ""
            val viewModel = siteCatalogItemViewModel(url = item)

            val element by viewModel.item.collectAsState()

            MangaAddScreen(nav, element)
        }
    )
}
