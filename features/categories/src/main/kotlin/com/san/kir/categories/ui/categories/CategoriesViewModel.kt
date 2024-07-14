package com.san.kir.categories.ui.categories

import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.categoryRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import kotlinx.coroutines.flow.map

internal class CategoriesViewModel(
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
) : ViewModel<CategoriesState>(), CategoriesStateHolder {

    override val tempState = categoryRepository.items.map(::CategoriesState)

    override val defaultState = CategoriesState()

    override suspend fun onAction(action: Action) {
        when (action) {
            is CategoriesAction.Reorder -> categoryRepository.swap(action.from, action.to)
            is CategoriesAction.ChangeVisibility -> {
                categoryRepository.insert(action.item.copy(isVisible = action.newState))
            }
        }
    }
}
