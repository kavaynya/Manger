package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.custom.DbNameAndId
import com.san.kir.data.models.main.NameAndId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbNameAndId.toModel() = NameAndId(id, name)

@JvmName("toNameAndIdModels")
internal fun List<DbNameAndId>.toModels() = map(DbNameAndId::toModel)

@JvmName("toFlowNameAndIdModel")
internal fun Flow<DbNameAndId?>.toModel() = map { it?.toModel() }

@JvmName("toFlowNameAndIdModels")
internal fun Flow<List<DbNameAndId>>.toModels() = map { it.toModels() }
