package com.san.kir.data.db.main.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_manga")
internal data class DbAccountManga(
    @ColumnInfo("id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("account_id") val accountId: Long = -1,
    @ColumnInfo("id_in_account") val idInAccount: Long = -1,
    @ColumnInfo("id_in_site") val idInSite: Long = -1,
    @ColumnInfo("id_in_library") val idInLibrary: Long = -1,
    @ColumnInfo("data") val data: String = ""
)
