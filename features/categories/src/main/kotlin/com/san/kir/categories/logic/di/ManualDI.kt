package com.san.kir.categories.logic.di

import com.san.kir.categories.logic.repo.CategoryRepository
import com.san.kir.core.utils.ManualDI
import com.san.kir.data.categoryDao

internal val ManualDI.categoryRepository: CategoryRepository
    get() = CategoryRepository(categoryDao)
