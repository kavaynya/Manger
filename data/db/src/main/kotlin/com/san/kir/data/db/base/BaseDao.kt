package com.san.kir.data.db.base

import androidx.room.Delete
import androidx.room.Upsert

interface BaseDao<in T> {
    @Upsert
    suspend fun insert(vararg item: T): List<Long>

    @Upsert
    suspend fun insert(items: List<T>): List<Long>

    @Delete
    suspend fun delete(vararg item: T): Int

    @Delete
    suspend fun delete(items: List<T>): Int
}
