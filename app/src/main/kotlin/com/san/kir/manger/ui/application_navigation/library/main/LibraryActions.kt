package com.san.kir.manger.ui.application_navigation.library.main

import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.san.kir.manger.R
import com.san.kir.manger.foreground_work.services.AppUpdateService
import com.san.kir.manger.foreground_work.services.MangaUpdaterService
import com.san.kir.manger.ui.application_navigation.library.LibraryNavTarget
import com.san.kir.core.compose_utils.MenuIcon
import com.san.kir.core.compose_utils.MenuText
import com.san.kir.manger.utils.compose.navigate

@Composable
fun LibraryActions(
    nav: NavHostController,
    viewModel: LibraryViewModel,
) {
    val categories by viewModel.preparedCategories.collectAsState(emptyList())
    val currentCategoryWithMangas by viewModel.currentCategoryWithManga.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    MenuIcon(icon = Icons.Default.Add) {
        nav.navigate(LibraryNavTarget.AddOnline)
    }

    MenuIcon(icon = Icons.Default.MoreVert) {
        expanded = true
    }

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

        MenuText(id = R.string.library_menu_reload) {
            expanded = false
            currentCategoryWithMangas.mangas.forEach {
                MangaUpdaterService.add(context, it)
            }
        }

        MenuText(id = R.string.library_menu_reload_all) {
            expanded = false
            categories.flatMap { it.mangas }.forEach {
                MangaUpdaterService.add(context, it)
            }
        }

        MenuText(id = R.string.library_menu_update) {
            expanded = false
            AppUpdateService.start(context)
        }
    }
}