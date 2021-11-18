package com.san.kir.manger.ui.application_navigation.additional_manga_screens

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.san.kir.manger.R
import com.san.kir.manger.di.DefaultDispatcher
import com.san.kir.manger.di.MainDispatcher
import com.san.kir.manger.room.dao.CategoryDao
import com.san.kir.manger.room.dao.MangaDao
import com.san.kir.manger.room.entities.Manga
import com.san.kir.manger.utils.extensions.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MangaEditViewModel @Inject constructor(
    ctx: Application,
    categoryDao: CategoryDao,
    private val mangaDao: MangaDao,
    @DefaultDispatcher private val default: CoroutineDispatcher,
    @MainDispatcher private val main: CoroutineDispatcher,
) : ViewModel() {
    var mangaUnic by mutableStateOf("")
    var manga by mutableStateOf(Manga())

    val categoryNames = categoryDao.loadItems().map { l -> l.map { it.name } }
    val statuses by lazy {
        listOf(
            ctx.getString(R.string.manga_status_unknown),
            ctx.getString(R.string.manga_status_continue),
            ctx.getString(R.string.manga_status_complete)
        )
    }

    init {
        snapshotFlow { mangaUnic }
            .flatMapLatest {
                log(it)
                val manga = mangaDao.loadItem(it)
                manga
            }
            .filterNotNull()
            .onEach {
                if (it.name.isNotEmpty())
                    withContext(main) {
                        manga = it
                    }
            }.launchIn(viewModelScope)
    }

    fun update() = viewModelScope.launch(default) { mangaDao.update(manga) }
}