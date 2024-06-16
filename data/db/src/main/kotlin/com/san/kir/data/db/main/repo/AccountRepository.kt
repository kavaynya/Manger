package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.AccountDao
import com.san.kir.data.db.main.entites.DbAccount
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.Account
import com.san.kir.data.models.main.data
import com.san.kir.data.models.utils.AccountType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public class AccountRepository internal constructor(private val accountDao: AccountDao) {

    public val items: Flow<List<Account>> = accountDao.loadItems().toModels()
    public fun loadItem(id: Long): Flow<Account?> = accountDao.loadItem(id).toModel()
    public suspend fun items(): List<Account> = withIoContext { accountDao.items().toModels() }
    public suspend fun item(id: Long): Account? = withIoContext { accountDao.item(id)?.toModel() }
    public suspend inline fun <reified Data> data(id: Long): Data? = item(id)?.data<Data>()
    public inline fun <reified Data> loadData(id: Long): Flow<Data?> =
        loadItem(id).map { it?.data<Data>() }

    public suspend fun delete(id: Long): Int = withIoContext { accountDao.delete(id) }
    public suspend fun update(
        id: Long,
        data: String,
        type: AccountType = AccountType.Shikimori
    ): List<Long> = withIoContext { accountDao.insert(DbAccount(id, type, data)) }
}
