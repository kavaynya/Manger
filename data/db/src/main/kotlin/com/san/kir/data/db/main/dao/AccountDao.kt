package com.san.kir.data.db.main.dao

import androidx.room.Dao
import androidx.room.Query
import com.san.kir.data.db.base.BaseDao
import com.san.kir.data.db.main.entites.DbAccount
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AccountDao : BaseDao<DbAccount> {

    @Query("SELECT * FROM accounts")
    fun loadItems(): Flow<List<DbAccount>>

    @Query("SELECT * FROM accounts WHERE id = :itemId")
    fun loadItem(itemId: Long): Flow<DbAccount?>

    @Query("SELECT * FROM accounts")
    suspend fun items(): List<DbAccount>

    @Query("SELECT * FROM accounts WHERE id = :itemId")
    suspend fun item(itemId: Long): DbAccount?

    @Query("UPDATE accounts SET data = :data WHERE id = :itemId")
    suspend fun update(itemId: Long, data: String): Int

    @Query("DELETE FROM accounts WHERE id = :itemId")
    suspend fun delete(itemId: Long): Int
}
