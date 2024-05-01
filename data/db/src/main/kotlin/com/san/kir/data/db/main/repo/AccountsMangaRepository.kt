package com.san.kir.data.db.main.repo

import com.san.kir.core.utils.coroutines.withIoContext
import com.san.kir.data.db.main.dao.AccountMangaDao
import com.san.kir.data.db.main.entites.DbAccountManga
import com.san.kir.data.db.main.mappers.toEntities
import com.san.kir.data.db.main.mappers.toModel
import com.san.kir.data.db.main.mappers.toModels
import com.san.kir.data.models.main.AccountManga


class AccountsMangaRepository internal constructor(private val accountMangaDao: AccountMangaDao) {
    fun loadItems(accountId: Long) = accountMangaDao.loadItems(accountId).toModels()

    fun loadItemByIdInAccount(accountId: Long, idInAccount: Long) =
        accountMangaDao.loadItemByTargetId(accountId, idInAccount).toModel()

    suspend fun itemByIdInLibrary(accountId: Long, idInLibrary: Long) =
        withIoContext { accountMangaDao.itemByLibId(accountId, idInLibrary)?.toModel() }

    suspend fun itemByIdInSite(accountId: Long, idInSite: Long) =
        withIoContext { accountMangaDao.itemByMangaId(accountId, idInSite)?.toModel() }

    suspend fun setIdInLibrary(idInLibrary: Long, accountId: Long, idInAccount: Long) =
        withIoContext { accountMangaDao.setIdInLibrary(accountId, idInAccount, idInLibrary) }

    suspend fun resetIdInLibrary(idInLibrary: Long, accountId: Long) =
        withIoContext { accountMangaDao.resetIdInLibrary(accountId, idInLibrary) }

    suspend fun insert(accountId: Long, idInAccount: Long, idInSite: Long, idInLibrary: Long = -1) =
        withIoContext {
            accountMangaDao.insert(
                DbAccountManga(0, accountId, idInAccount, idInSite, idInLibrary, "{}")
            )
        }

    suspend fun insert(list: List<AccountManga>) =
        withIoContext { accountMangaDao.insert(list.toEntities()) }

    suspend fun updateData(accountId: Long, idInAccount: Long, data: String) =
        withIoContext { accountMangaDao.updateDataByTargetId(accountId, idInAccount, data) }

    suspend fun delete(accountId: Long, idInAccount: Long) =
        withIoContext { accountMangaDao.deleteByTargetId(accountId, idInAccount) }

}
