package com.san.kir.library.ui.mangaAbout

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.getFullPath
import com.san.kir.core.utils.lengthMb
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.categoryRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.main.Manga
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
internal class MangaAboutViewModel(
    private val mangaId: Long,
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
) : ViewModel<MangaAboutState>(), MangaAboutStateHolder {

    private val manga = MutableStateFlow(Manga())
    private val size = manga.map { getFullPath(it.path).lengthMb }.stateInSubscribed(0.0)
    private val categoryName = manga
        .flatMapLatest { categoryRepository.categoryName(it.categoryId) }
        .stateInSubscribed("")

    init {
        defaultLaunch {
            mangaRepository.loadItem(mangaId).filterNotNull().collect { manga.value = it }
        }
    }

    override val tempState = combine(manga, categoryName, size, ::MangaAboutState)
    override val defaultState = MangaAboutState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is MangaAboutAction.ChangeUpdate -> change(action.newState)
        }
    }

    private suspend fun change(isUpdate: Boolean) {
        manga.update { it.copy(isUpdate = isUpdate) }
        mangaRepository.save(manga.value)
    }
}
