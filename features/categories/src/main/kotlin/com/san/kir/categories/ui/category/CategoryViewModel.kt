package com.san.kir.categories.ui.category

import android.content.Context
import com.san.kir.background.works.RemoveCategoryWorker
import com.san.kir.categories.logic.di.categoryRepository
import com.san.kir.categories.logic.repo.CategoryRepository
import com.san.kir.core.support.CATEGORY_ALL
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.ScreenEvent
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.models.utils.CATEGORY_ALL
import com.san.kir.core.utils.viewModel.BaseViewModel
import com.san.kir.data.models.base.Category
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class CategoryViewModel(
    private val context: Context = ManualDI.context,
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository,
) : ViewModel<CategoryState>(), CategoryStateHolder {

    private val hasCreatedNew = MutableStateFlow(false)
    private val currentCategory = MutableStateFlow(Category())
    private val oldCategoryName = MutableStateFlow("")
    private val hasChanges = MutableStateFlow(false)

    override val tempState = combine(
        currentCategory,
        hasCreatedNew,
        categoryRepository.names,
        oldCategoryName,
        hasChanges
    ) { cat, hasCreatedNew, items, oldName, changes ->
        CategoryState(
            category = cat,
            categoryNames = items.toPersistentList(),
            hasCreatedNew = hasCreatedNew,
            oldCategoryName = oldName,
            hasAll = oldName == context.CATEGORY_ALL,
            hasChanges = changes,
        )
    }

    override val defaultState = CategoryState()

    override suspend fun onEvent(event: ScreenEvent) {
        when (event) {
            CategoryEvent.Save -> save()
            is CategoryEvent.Set -> setCategory(event.categoryName)
            CategoryEvent.Delete -> RemoveCategoryWorker.addTask(context, currentCategory.value)
            is CategoryEvent.Update -> change(event)
        }
    }

    private suspend fun save() {
        hasChanges.update { false }
        if (hasCreatedNew.value) {
            categoryRepository.insert(currentCategory.value)
        } else {
            categoryRepository.update(currentCategory.value)
        }
    }

    private suspend fun setCategory(categoryName: String) {
        if (categoryName.isEmpty()) hasCreatedNew.update { true }
        currentCategory.update { categoryRepository.item(categoryName) }
        oldCategoryName.update { categoryName }
    }

    private fun change(event: CategoryEvent.Update) {
        event.newName?.let { newName ->
            currentCategory.update { it.copy(name = newName) }
        }

        event.newTypeSort?.let { newTypeSort ->
            currentCategory.update { it.copy(typeSort = newTypeSort) }
        }

        event.newReverseSort?.let { newReverseSort ->
            currentCategory.update { it.copy(isReverseSort = newReverseSort) }
        }

        event.newVisible?.let { newVisible ->
            currentCategory.update { it.copy(isVisible = newVisible) }
        }

        event.newLargePortrait?.let { newLarge ->
            currentCategory.update { it.copy(isLargePortrait = newLarge) }
        }

        event.newSpanPortrait?.let { newSpan ->
            currentCategory.update { it.copy(spanPortrait = newSpan) }
        }

        event.newLargeLandscape?.let { newLarge ->
            currentCategory.update { it.copy(isLargeLandscape = newLarge) }
        }

        event.newSpanLandscape?.let { newSpan ->
            currentCategory.update { it.copy(spanLandscape = newSpan) }
        }

        hasChanges.update { true }
    }
}
