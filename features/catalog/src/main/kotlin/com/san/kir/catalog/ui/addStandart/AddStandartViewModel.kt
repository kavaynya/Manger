package com.san.kir.catalog.ui.addStandart

import com.san.kir.background.logic.UpdateMangaManager
import com.san.kir.background.logic.di.updateMangaManager
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.catalogsRepository
import com.san.kir.data.categoryRepository
import com.san.kir.data.db.catalog.repo.CatalogsRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.db.main.repo.MangaRepository
import com.san.kir.data.db.main.repo.StatisticsRepository
import com.san.kir.data.mangaRepository
import com.san.kir.data.models.catalog.toManga
import com.san.kir.data.parsing.SiteCatalogAlternative
import com.san.kir.data.parsing.SiteCatalogsManager
import com.san.kir.data.parsing.siteCatalogsManager
import com.san.kir.data.statisticsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds


internal class AddStandartViewModel(
    private val url: String,
    private val catalogRepository: CatalogsRepository = ManualDI.catalogsRepository(),
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
    private val mangaRepository: MangaRepository = ManualDI.mangaRepository(),
    private val statisticRepository: StatisticsRepository = ManualDI.statisticsRepository(),
    private val manager: SiteCatalogsManager = ManualDI.siteCatalogsManager(),
    private val updateManager: UpdateMangaManager = ManualDI.updateMangaManager(),
) : ViewModel<AddStandartState>(), AddStandartStateHolder {
    private val categoryName = MutableStateFlow("")
    private val processState = MutableStateFlow<ProcessState>(ProcessState.None)
    private val progress = MutableStateFlow(0)

    override val tempState = combine(
        categoryName, categoryRepository.names, processState, progress,
    ) { category, categories, process, progress ->
        val filteredCategories = categories.filter { category in it }
        AddStandartState(
            categoryName = category,
            availableCategories = filteredCategories,
            processState = process,
            progress = progress
        )
    }

    override val defaultState = AddStandartState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is AddStandartAction.UpdateText -> categoryName.value = action.text
            AddStandartAction.StartProcess -> startProcess()
        }
    }

    private suspend fun startProcess() {
        kotlin.runCatching {
            processState.update { ProcessState.Load }

            if (state.value.createNewCategory) {
                categoryRepository.insert(categoryName.value)
                delay(1.seconds)
            }

            progress.update { ProcessStatus.CATEGORY_CHANGED }

            progress.update { ProcessStatus.PREV_AND_UPDATE_MANGA }

            val element = manager.elementByUrl(url) ?: throw NullPointerException()
            val matcher = Pattern.compile("[a-z/0-9]+-").matcher(element.shortLink)
            var shortPath = element.shortLink
            if (matcher.find())
                shortPath = element.shortLink
                    .removePrefix("/")
                    .removePrefix("/")
                    .replace("/", "_")
                    .removeSuffix(".html")

            val path = "${DIR.MANGA}/${element.catalogName}/$shortPath"
            val mangaId = mangaRepository.save(
                element.toManga(
                    categoryId = categoryRepository.idByName(categoryName.value),
                    path = path
                ).copy(
                    isAlternativeSite = manager.catalog(element.link) is SiteCatalogAlternative
                )
            ).ifEmpty { throw ArrayIndexOutOfBoundsException() }.first()

            statisticRepository.insert(mangaId)

            progress.update { ProcessStatus.PREV_AND_CREATED_FOLDER }

            catalogRepository.insert(manager.catalogName(element.catalogName), element)
            delay(1.seconds)

            progress.update { ProcessStatus.PREV_AND_SEARCH_CHAPTERS }

            updateManager.addTask(mangaId)
            delay(1.seconds)

            progress.update { ProcessStatus.ALL_COMPLETE }

        }.onFailure {
            processState.update { ProcessState.Error }
            Timber.e(it)
        }.onSuccess {
            processState.update { ProcessState.Complete }
        }
    }

}
