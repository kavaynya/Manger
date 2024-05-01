package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.AccountDao
import com.san.kir.data.db.main.entites.DbAccount
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.data
import com.san.kir.data.models.utils.AccountType
import kotlinx.coroutines.flow.map

class AccountRepository internal constructor(private val accountDao: AccountDao) {

    val items = accountDao.loadItems().toModels()
    fun loadItem(id: Long) = accountDao.loadItem(id).toModel()
    suspend fun items() = withIoContext { accountDao.items().toModels() }
    suspend fun item(id: Long) = withIoContext { accountDao.item(id)?.toModel() }
    suspend inline fun <reified Data> data(id: Long) = item(id)?.data<Data>()
    inline fun <reified Data> loadData(id: Long) = loadItem(id).map { it?.data<Data>() }
    suspend fun delete(id: Long) = withIoContext { accountDao.delete(id) }
    suspend fun update(id: Long, data: String, type: AccountType = AccountType.Shikimori) =
        withIoContext { accountDao.insert(DbAccount(id, type, data)) }
}
