package com.san.kir.categories.ui.category

import android.content.Context
import com.san.kir.background.works.RemoveCategoryWorker
import com.san.kir.categories.R
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.categoryAll
import com.san.kir.core.utils.coroutines.defaultLaunch
import com.san.kir.core.utils.viewModel.Action
import com.san.kir.core.utils.viewModel.ViewModel
import com.san.kir.data.categoryRepository
import com.san.kir.data.db.main.repo.CategoryRepository
import com.san.kir.data.models.main.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class CategoryViewModel(
    private val categoryName: String,
    private val context: Context = ManualDI.application,
    private val categoryRepository: CategoryRepository = ManualDI.categoryRepository(),
) : ViewModel<CategoryState>(), CategoryStateHolder {

    private val tooShortString = context.getString(R.string.too_short)
    private val nameIsBusyString = context.getString(R.string.name_taken)
    private var categoryNames: List<String>? = null

    private val isCreatedNew = categoryName.isNotEmpty()
    private val currentCategory = MutableStateFlow(Category())
    private val hasChanges = MutableStateFlow(false)
    private val error = MutableStateFlow<ErrorState>(ErrorState.None)

    override val tempState = combine(
        currentCategory,
        hasChanges,
        error
    ) { cat, changes, error ->
        CategoryState(
            category = cat,
            isCreatedNew = isCreatedNew,
            oldCategoryName = categoryName,
            hasAll = categoryName == ManualDI.categoryAll(),
            hasChanges = changes,
            error = error
        )
    }

    override val defaultState = CategoryState()

    init {
        defaultLaunch {
            currentCategory.value = categoryRepository.item(categoryName)
            checkCategoryName(categoryName)
        }
    }

    override suspend fun onAction(action: Action) {
        when (action) {
            CategoryAction.Save -> save()
            CategoryAction.Delete -> RemoveCategoryWorker.addTask(currentCategory.value)
            is CategoryAction.Update -> change(action)
        }
    }

    private suspend fun save() {
        hasChanges.value = false
        categoryRepository.insert(currentCategory.value)
    }

    private fun change(event: CategoryAction.Update) {
        event.newName?.let { newName ->
            currentCategory.update { it.copy(name = newName) }
            checkCategoryName(newName)
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

        event.newSpanPortrait?.let { newSpan ->
            currentCategory.update { it.copy(spanPortrait = newSpan) }
        }

        event.newSpanLandscape?.let { newSpan ->
            currentCategory.update { it.copy(spanLandscape = newSpan) }
        }

        hasChanges.value = true
    }

    private fun checkCategoryName(newName: String) = defaultLaunch {
        var validateText = ""
        if (newName.length < 3) {
            validateText = tooShortString
        } else if (newName != categoryName) {
            val names = categoryNames ?: categoryRepository.names()
            categoryNames = names

            if (newName in names) {
                validateText = nameIsBusyString
            }
        }

        error.value = if (validateText.isEmpty()) ErrorState.None else ErrorState.Has(validateText)
    }
}
