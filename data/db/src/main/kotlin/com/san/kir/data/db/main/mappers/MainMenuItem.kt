package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbMainMenuItem
import com.san.kir.data.models.main.MainMenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbMainMenuItem.toModel() = MainMenuItem(id, name, isVisible, order, type)

@JvmName("toMainMenuItemModels")
internal fun List<DbMainMenuItem>.toModels() = map(DbMainMenuItem::toModel)

@JvmName("toFlowMainMenuItemModel")
internal fun Flow<DbMainMenuItem?>.toModel() = map { it?.toModel() }

@JvmName("toFlowMainMenuItemModels")
internal fun Flow<List<DbMainMenuItem>>.toModels() = map { it.toModels() }


internal fun MainMenuItem.toEntity() = DbMainMenuItem(id, name, isVisible, order, type)

@JvmName("toMainMenuItemEntities")
internal fun List<MainMenuItem>.toEntities() = map(MainMenuItem::toEntity)
