package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbStorage
import com.san.kir.data.models.main.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbStorage.toModel() = Storage(id, name, path, sizeFull, sizeRead, catalogName)

@JvmName("toStorageModels")
internal fun List<DbStorage>.toModels() = map(DbStorage::toModel)

@JvmName("toFlowStorageModel")
internal fun Flow<DbStorage?>.toModel() = map { it?.toModel() }

@JvmName("toFlowStorageModels")
internal fun Flow<List<DbStorage>>.toModels() = map { it.toModels() }


internal fun Storage.toEntity() = DbStorage(id, name, path, sizeFull, sizeRead, false, catalogName)

@JvmName("toStorageEntities")
internal fun List<Storage>.toEntities() = map(Storage::toEntity)
