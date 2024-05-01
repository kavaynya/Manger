package com.san.kir.library.ui.mangaAbout

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.lengthMb
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.base.Manga
import com.san.kir.library.logic.di.mangaRepository
import com.san.kir.library.logic.repo.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class MangaAboutViewModel(
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository,
) : ViewModel<MangaAboutState>(), MangaAboutStateHolder {

    private val manga = MutableStateFlow(Manga())
    private val categoryName = MutableStateFlow("")
    private val size = MutableStateFlow(0.0)

    override val tempState = combine(manga, categoryName, size, ::MangaAboutState)

    override val defaultState = MangaAboutState()

    override suspend fun onEvent(event: Action) {
        when (event) {
            is MangaAboutEvent.Set -> set(event.id)
            is MangaAboutEvent.ChangeUpdate -> change(event.newState)
            is MangaAboutEvent.ChangeColor -> change(event.newState)
        }
    }

    private suspend fun set(mangaId: Long) {
        manga.update { mangaRepository.item(mangaId) }
        categoryName.update { mangaRepository.categoryName(manga.value.categoryId) }
        size.update { getFullPath(manga.value.path).lengthMb }
    }

    private suspend fun change(isUpdate: Boolean) {
        manga.update { it.copy(isUpdate = isUpdate) }
        mangaRepository.update(manga.value)
    }

    private suspend fun change(color: Int) {
        manga.update { it.copy(color = color) }
        mangaRepository.update(manga.value)
    }
}
