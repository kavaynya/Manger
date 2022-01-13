package com.san.kir.manger.ui.application_navigation.storage

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.san.kir.manger.ui.application_navigation.additional_manga_screens.MangaStorageScreen
import com.san.kir.manger.ui.application_navigation.additional_manga_screens.mangaStorageViewModel
import com.san.kir.manger.ui.application_navigation.storage.main.StorageScreen
import com.san.kir.manger.utils.compose.MangaItem
import com.san.kir.manger.utils.compose.NavItem
import com.san.kir.manger.utils.compose.NavTarget
import com.san.kir.manger.utils.compose.getStringElement

sealed class StorageNavTarget : NavTarget {
    object Main : StorageNavTarget() {
        override val route: String = "main"
    }

    object Storage : StorageNavTarget() {
        override val base: String = "manga_storage"
        override val item: NavItem = MangaItem
        override val isOptional: Boolean = true
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.storageNavGraph(nav: NavHostController) {
    composable(
        route = StorageNavTarget.Main.route,
        content = {
            StorageScreen(nav)
        }
    )

    composable(
        route = StorageNavTarget.Storage.route,
        content = {
            val item = nav.getStringElement(MangaItem) ?: ""
            val viewModel = mangaStorageViewModel(item)

            MangaStorageScreen(nav, viewModel)
        }
    )
}