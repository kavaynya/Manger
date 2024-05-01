package com.san.kir.library.ui.library

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.support.MainMenuType
import com.san.kir.data.models.utils.MainMenuType
import com.san.kir.core.utils.viewModel.ScreenState
import com.san.kir.data.models.extend.CategoryWithMangas
import com.san.kir.data.db.main.views.ViewManga

import kotlinx.collections.immutable.ImmutableMap


@Immutable
internal data class LibraryState(
    val selectedManga: SelectedMangaState = SelectedMangaState.NonVisible,
    val currentCategory: CategoryWithMangas = CategoryWithMangas(),
    val items: ItemsState = ItemsState.Load,
    val showCategory: Boolean = false,
    val background: BackgroundState = BackgroundState.None,
) : ScreenState

@Stable
internal sealed interface ItemsState {
    data object Empty : ItemsState
    data object Load : ItemsState

    @Stable
    data class Ok(
        val items: List<CategoryWithMangas>,
        //    Имена всех категорий и их id
        val categories: ImmutableMap<Long, String>,
        // Имена категорий с количество содержимой манги для заголовка вкладок
        val names: List<String> =
            items
                .map { cat -> "${cat.name}: ${cat.mangas.count()}" }
                .toImmutableList(),
    ) : ItemsState
}

@Stable
internal sealed interface SelectedMangaState {
    data object NonVisible : SelectedMangaState

    @Stable
    data class Visible(val item: SimplifiedManga) : SelectedMangaState {
        override fun toString() =
            "Visible(name=${item.name}, category=${item.category}, noRead=${item.noRead})"
    }
}

@Stable
internal sealed interface BackgroundState {
    data object Work : BackgroundState
    data object None : BackgroundState
}

//@Immutable
internal data class LibraryNavigation(
    val navigateToScreen: (MainMenuType) -> Unit,
    val navigateToInfo: (Long) -> Unit,
    val navigateToStorage: (Long, SharedParams) -> Unit,
    val navigateToStats: (Long, SharedParams) -> Unit,
    val navigateToChapters: (Long, SharedParams) -> Unit,
    val navigateToOnline: (SharedParams) -> Unit,
)
