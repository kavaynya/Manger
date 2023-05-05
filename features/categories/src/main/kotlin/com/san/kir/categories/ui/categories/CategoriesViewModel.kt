package com.san.kir.categories.ui.categories

import com.san.kir.categories.logic.di.categoryRepository
import com.san.kir.categories.logic.repo.CategoryRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.map

internal class CategoriesViewModel(
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository,
) : ViewModel<CategoriesState>(), CategoriesStateHolder {

    override val tempState = categoryRepository.items.map { CategoriesState(it.toPersistentList()) }

    override val defaultState = CategoriesState(persistentListOf())

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            is CategoriesEvent.Reorder -> categoryRepository.swap(event.from, event.to)
            is CategoriesEvent.ChangeVisibility -> {
                categoryRepository.update(event.item.copy(isVisible = event.newState))
            }
        }
    }
}
