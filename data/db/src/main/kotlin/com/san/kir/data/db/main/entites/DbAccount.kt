package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.san.kir.data.models.utils.AccountType

@Entity(tableName = "accounts")
internal data class DbAccount(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("name") val type: AccountType = AccountType.None,
    @ColumnInfo("data") val data: String = ""
)
