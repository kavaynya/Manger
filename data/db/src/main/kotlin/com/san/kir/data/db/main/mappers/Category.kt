package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbCategory
import com.san.kir.data.models.main.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbCategory.toModel() =
    Category(id, name, order, isVisible, typeSort, isReverseSort, spanPortrait, spanLandscape)

@JvmName("toCategoryModels")
internal fun List<DbCategory>.toModels() = map(DbCategory::toModel)

@JvmName("toFlowCategoryModel")
internal fun Flow<DbCategory?>.toModel() = map { it?.toModel() }

@JvmName("toFlowCategoryModels")
internal fun Flow<List<DbCategory>>.toModels() = map { it.toModels() }


internal fun Category.toEntity() =
    DbCategory(id, name, order, isVisible, typeSort, isReverseSort, spanPortrait, spanLandscape)
