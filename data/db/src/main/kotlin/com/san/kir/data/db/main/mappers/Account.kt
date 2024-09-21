package com.san.kir.data.db.main.mappers

import com.san.kir.data.db.main.entites.DbAccount
import com.san.kir.data.models.main.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun DbAccount.toModel() = Account(id, type, data)

@JvmName("toAccountModels")
internal fun List<DbAccount>.toModels() = map(DbAccount::toModel)

@JvmName("toFlowAccountModel")
internal fun Flow<DbAccount?>.toModel() = map { it?.toModel() }

@JvmName("toFlowAccountModels")
internal fun Flow<List<DbAccount>>.toModels() = map { it.toModels() }


internal fun Account.toEntity() = DbAccount(id, type, data)
