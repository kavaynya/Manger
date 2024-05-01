package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbAccountManga
import kotlinx.coroutines.flow.Flow


@Dao
internal interface AccountMangaDao : BaseDao<DbAccountManga> {
    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId")
    fun loadItems(accountId: Long): Flow<List<DbAccountManga>>

    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId AND id_in_account IS :idInAccount")
    fun loadItemByTargetId(accountId: Long, idInAccount: Long): Flow<DbAccountManga?>

    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId AND id_in_library IS :libId")
    fun loadItemByLibId(accountId: Long, libId: Long): Flow<DbAccountManga?>

    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId AND id_in_site IS :mangaId")
    suspend fun itemByMangaId(accountId: Long, mangaId: Long): DbAccountManga?

    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId AND id_in_account IS :idInAccount")
    suspend fun itemByTargetId(accountId: Long, idInAccount: Long): DbAccountManga?

    @Query("SELECT * FROM account_manga WHERE account_id IS :accountId AND id_in_library IS :idInLibrary")
    suspend fun itemByLibId(accountId: Long, idInLibrary: Long): DbAccountManga?

    @Query("UPDATE account_manga SET data = :data WHERE account_id IS :accountId AND id_in_account IS :idInAccount")
   suspend fun updateDataByTargetId(accountId: Long, idInAccount: Long, data: String?)

    @Query("UPDATE account_manga SET id_in_library = :idInLibrary WHERE account_id IS :accountId AND id_in_account IS :idInAccount")
    suspend fun setIdInLibrary(accountId: Long, idInAccount: Long, idInLibrary: Long)

    @Query("UPDATE account_manga SET id_in_library = -1 WHERE account_id IS :accountId AND id_in_library IS :idInLibrary")
    suspend fun resetIdInLibrary(accountId: Long, idInLibrary: Long)

    @Query("DELETE FROM account_manga WHERE account_id IS :accountId AND id_in_account IS :idInAccount")
    suspend fun deleteByTargetId(accountId: Long, idInAccount: Long)

    @Query("DELETE FROM account_manga WHERE account_id IS :accountId")
    suspend fun clear(accountId: Long)
}
